package com.smartcity.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "SMARTCITY_TEST_DATABASE_URL", matches = "jdbc:postgresql:.+")
class PostgresWorkflowTest {

    private static final String SCHEMA = "test_" + UUID.randomUUID().toString().replace("-", "");
    @TempDir static Path uploads;

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry properties) {
        String url = System.getenv("SMARTCITY_TEST_DATABASE_URL");
        properties.add("spring.datasource.url", () -> url);
        properties.add("spring.datasource.username", () -> System.getenv().getOrDefault("SMARTCITY_TEST_DB_USER", "app"));
        properties.add("spring.datasource.password", () -> System.getenv().getOrDefault("SMARTCITY_TEST_DB_PASSWORD", ""));
        properties.add("spring.datasource.hikari.connection-init-sql", () -> "SET search_path TO " + SCHEMA);
        properties.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/fixtures");
        properties.add("spring.flyway.enabled", () -> true);
        properties.add("spring.flyway.schemas", () -> SCHEMA);
        properties.add("spring.flyway.default-schema", () -> SCHEMA);
        properties.add("spring.flyway.placeholders.demo-enabled", () -> true);
        properties.add("spring.jpa.properties.hibernate.default_schema", () -> SCHEMA);
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        properties.add("app.demo.enabled", () -> true);
        properties.add("app.cv.directory", () -> uploads.toString());
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired Flyway flyway;

    @Test
    void migrationsStartAndSeedIsRepeatable() throws Exception {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("12");
        flyway.migrate();
        assertThat(jdbc.queryForObject("select count(*) from " + SCHEMA + ".userses where user_code = ?",
                Long.class, "casablanca-demo-resident")).isEqualTo(1);
        for (String endpoint : List.of("tourism_places", "educational_institutions", "sport_events", "jobs", "companies")) {
            mvc.perform(get("/api/" + endpoint)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").isNumber());
        }
    }

    @Test
    void tourismCrudPersistsAndDeletesTheJoinedRow() throws Exception {
        long city = idOf(mvc.perform(get("/api/cities")).andReturn(), true);
        String body = """
                {"name":"Integration walk","description":"Test listing","latitude":33.58,"longitude":-7.64,
                 "address":"Maarif","cityId":%d,"category":"Culture","openingHours":"Daily","rating":4}
                """.formatted(city);
        String api = "/api/tourism_places";
        long id = idOf(mvc.perform(post(api).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn(), false);
        String path = api + "/" + id;
        mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(body.replace("Integration walk", "Edited walk")))
                .andExpect(status().isOk());
        mvc.perform(get(path)).andExpect(jsonPath("$.name").value("Edited walk"));
        assertThat(jdbc.queryForObject("select p.name from " + SCHEMA + ".places p join " + SCHEMA
                + ".tourism_places t using (id) where t.id = ?", String.class, id)).isEqualTo("Edited walk");
        mvc.perform(delete(path)).andExpect(status().isNoContent());
        mvc.perform(get(path)).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("select count(*) from " + SCHEMA + ".places where id = ?", Long.class, id)).isZero();
    }

    @Test
    void concurrentReservationsRespectCapacityAndCancellationIsIdempotent() throws Exception {
        long event = idOf(mvc.perform(get("/api/sport_events")).andReturn(), true);
        String request = "{\"eventId\":" + event + ",\"quantity\":40}";
        var pool = Executors.newFixedThreadPool(2);
        var simultaneous = new CountDownLatch(1);
        Callable<MvcResult> reserve = () -> {
            simultaneous.await();
            return mvc.perform(post("/api/bookings/reservations").session(resident(false)).contentType(MediaType.APPLICATION_JSON).content(request)).andReturn();
        };
        long booking;
        try {
            var first = pool.submit(reserve);
            var second = pool.submit(reserve);
            simultaneous.countDown();
            var results = List.of(first.get(), second.get());
            assertThat(results.stream().map(result -> result.getResponse().getStatus()).toList())
                    .containsExactlyInAnyOrder(201, 409);
            booking = idOf(results.stream().filter(result -> result.getResponse().getStatus() == 201).findFirst().orElseThrow(), false);
        } finally {
            pool.shutdownNow();
        }
        mvc.perform(get("/api/sport_events/" + event)).andExpect(jsonPath("$.availableTickets").value(10));
        mvc.perform(get("/api/bookings/mine").session(resident(false))).andExpect(jsonPath("$[0].quantity").value(40))
                .andExpect(jsonPath("$[0].unitPrice").value(40)).andExpect(jsonPath("$[0].status").value("CONFIRMED"));
        for (int attempt = 0; attempt < 2; attempt++) {
            mvc.perform(post("/api/bookings/" + booking + "/cancel").session(resident(false))).andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELLED"));
        }
        mvc.perform(get("/api/sport_events/" + event)).andExpect(jsonPath("$.availableTickets").value(50));
        assertThat(jdbc.queryForObject("select reserved_tickets from " + SCHEMA + ".sport_events where id = ?", Integer.class, event))
                .isZero();
    }

    @Test
    void uploadsRetrievesAndSubmitsASampleCv() throws Exception {
        byte[] contents = "%PDF-1.4\n% Fictional test CV\n%%EOF\n".getBytes(StandardCharsets.US_ASCII);
        var file = new MockMultipartFile("file", "sample-cv.pdf", "application/pdf", contents);
        long cv = idOf(mvc.perform(multipart("/api/cvs/upload").file(file).session(resident(false)).param("title", "Fictional CV"))
                .andExpect(status().isCreated()).andReturn(), false);
        mvc.perform(get("/api/cvs/" + cv + "/file").session(resident(false))).andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff")).andExpect(content().bytes(contents));
        assertThat(uploads.resolve(jdbc.queryForObject("select storage_path from " + SCHEMA + ".cvs where id = ?", String.class, cv)))
                .exists();
        long job = idOf(mvc.perform(get("/api/jobs")).andReturn(), true);
        String body = """
                {"jobId":%d,"cvId":%d,"coverNote":"Sample portfolio attached"}
                """.formatted(job, cv);
        long application = idOf(mvc.perform(post("/api/job_applications/submissions").session(resident(false)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING")).andReturn(), false);
        mvc.perform(post("/api/job_applications/submissions").session(resident(false)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
        mvc.perform(patch("/api/job_applications/" + application + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACCEPTED\"}"))
                .andExpect(status().isOk());
        String review = "/api/cvs/" + cv + "/review";
        mvc.perform(get(review).session(resident(true))).andExpect(status().isForbidden());
        var reviewer = resident(true);
        Long reviewerId = (Long) reviewer.getAttribute(com.smartcity.service.CurrentResident.SESSION_USER);
        Long adminId = jdbc.queryForObject("insert into admins(adminid, authorization_level) values (?,1) returning id",
                Long.class, reviewerId);
        jdbc.update("update userses set admin_id = ? where id = ?", adminId, reviewerId);
        try {
            mvc.perform(get(review).session(reviewer)).andExpect(status().isOk()).andExpect(content().bytes(contents));
        } finally {
            jdbc.update("update userses set admin_id = null where id = ?", reviewerId);
            jdbc.update("delete from admins where id = ?", adminId);
        }
        mvc.perform(get("/api/job_applications/mine").session(resident(false))).andExpect(jsonPath("$[0].status").value("ACCEPTED"))
                .andExpect(jsonPath("$[0].coverNote").value("Sample portfolio attached"));
        assertThat(jdbc.queryForObject("select c_v_id from " + SCHEMA + ".job_applications where id = ?", Long.class, application))
                .isEqualTo(cv);
    }

    @Test
    void publishingAndUnpublishingChangesThePublicNewsFeed() throws Exception {
        long company = idOf(mvc.perform(get("/api/companies")).andReturn(), true);
        String payload = """
                {"companyId":%d,"title":"Workflow test announcement","body":"Fictional update","category":"ANNOUNCEMENT","published":false}
                """.formatted(company);
        long news = idOf(mvc.perform(post("/api/business_news").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated()).andReturn(), false);
        mvc.perform(get("/api/business_news")).andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Workflow test announcement"))));
        mvc.perform(put("/api/business_news/" + news).contentType(MediaType.APPLICATION_JSON).content(payload.replace("false", "true")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.publishedAt").isNotEmpty());
        mvc.perform(get("/api/business_news")).andExpect(content().string(org.hamcrest.Matchers.containsString("Workflow test announcement")));
        mvc.perform(put("/api/business_news/" + news).contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk()).andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void studentCvsCanBeRenamedReplacedAndDeletedByTheirOwner() throws Exception {
        MockHttpSession second = resident(true);
        byte[] original = "%PDF-1.4\n% Original\n".getBytes(StandardCharsets.US_ASCII);
        byte[] replacement = "%PDF-1.4\n% Replacement\n".getBytes(StandardCharsets.US_ASCII);
        long cv = idOf(mvc.perform(multipart("/api/cvs/upload")
                .file(new MockMultipartFile("file", "student.pdf", "application/pdf", original)).session(second).param("title", "Student PDF"))
                .andExpect(status().isCreated()).andReturn(), false);
        String fileUrl = "/api/cvs/" + cv + "/file";
        String editUrl = "/api/cvs/mine/" + cv;
        mvc.perform(get(fileUrl).session(resident(false))).andExpect(status().isNotFound());
        mvc.perform(get("/api/cvs/" + cv)).andExpect(status().isNotFound());
        mvc.perform(delete(editUrl).session(resident(false))).andExpect(status().isNotFound());
        mvc.perform(multipart(org.springframework.http.HttpMethod.PUT, editUrl)
                .file(new MockMultipartFile("file", "replacement.pdf", "application/pdf", replacement)).session(second).param("title", "Updated student PDF"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Updated student PDF"));
        mvc.perform(get(fileUrl).session(second)).andExpect(status().isOk()).andExpect(content().bytes(replacement));
        mvc.perform(delete(editUrl).session(second)).andExpect(status().isNoContent());
        mvc.perform(get(fileUrl).session(second)).andExpect(status().isNotFound());
    }

    @Test
    void favouritesAreIdempotentStudentScopedAndAudited() throws Exception {
        long place = idOf(mvc.perform(get("/api/tourism_places")).andReturn(), true);
        MockHttpSession second = resident(true);
        String route = "/api/favourites/tourism/" + place;
        for (int i = 0; i < 2; i++) {
            mvc.perform(put(route).session(second)).andExpect(status().isNoContent());
        }
        mvc.perform(get("/api/favourites").session(resident(false))).andExpect(content().json("[]"));
        mvc.perform(get("/api/favourites").session(second)).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].module").value("tourism")).andExpect(jsonPath("$[0].recordId").value(place));
        mvc.perform(delete(route).session(second)).andExpect(status().isNoContent());
        mvc.perform(get("/api/favourites").session(second)).andExpect(content().json("[]"));
        mvc.perform(get("/api/audit_history")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resource == 'favourites')].operation")
                        .value(org.hamcrest.Matchers.containsInAnyOrder("INSERT", "DELETE")));
    }

    @Test
    void migrationSchedulesCompatibleLegacyEventsWithoutChangingTheirPublishedTime() {
        String schema = SCHEMA + "_legacy";
        // Simulate an invalid price originally accepted by the pre-Flyway schema.
        var old = Flyway.configure().dataSource(flyway.getConfiguration().getDataSource())
                .schemas(schema).defaultSchema(schema).locations("classpath:db/migration", "classpath:db/fixtures").placeholders(Map.of("demo-enabled", "true")).target("5").load();
        old.migrate();
        jdbc.update("insert into " + schema + ".sport_events(title,description,event_date,ticket_price,venue_id) "
                + "select 'Legacy scheduled fixture','',timestamp '2099-01-02 10:00:00',10,venue_id from "
                + schema + ".sport_events limit 1");
        jdbc.update("insert into " + schema + ".sport_events(title,description,event_date,ticket_price,venue_id) "
                + "select 'Legacy conflicting fixture','',event_date,10,venue_id from " + schema
                + ".sport_events where title = 'Legacy scheduled fixture'");
        jdbc.execute("alter table " + schema + ".sport_events drop constraint event_price_check");
        jdbc.update("insert into " + schema + ".sport_events(title,description,event_date,ticket_price,venue_id) "
                + "select 'Legacy invalid price','',timestamp '2099-01-04 10:00:00',-1,venue_id from "
                + schema + ".sport_events limit 1");
        jdbc.execute("alter table " + schema + ".sport_events add constraint event_price_check "
                + "check (ticket_price >= 0 and ticket_price < 'Infinity'::float8) not valid");
        Flyway.configure().dataSource(flyway.getConfiguration().getDataSource())
                .schemas(schema).defaultSchema(schema).locations("classpath:db/migration", "classpath:db/fixtures").placeholders(Map.of("demo-enabled", "true")).load().migrate();
        assertThat(jdbc.queryForObject("select e.event_date = s.starts_at and e.ticket_capacity = 0 from " + schema
                + ".sport_events e join " + schema + ".venue_slots s on s.id = e.slot_id where e.title = 'Legacy scheduled fixture'",
                Boolean.class)).isTrue();
        assertThat(jdbc.queryForObject("select slot_id is null from " + schema
                + ".sport_events where title = 'Legacy conflicting fixture'", Boolean.class)).isTrue();
        assertThat(jdbc.queryForObject("select slot_id is null and ticket_price = -1 from " + schema
                + ".sport_events where title = 'Legacy invalid price'", Boolean.class)).isTrue();
    }

    @Test
    void demoAnalyticsReportsOnlySeededAggregateCountsAndDisabledCollectionWritesNothing() throws Exception {
        mvc.perform(get("/api/atlas-visits/collection")).andExpect(jsonPath("$.enabled").value(false));
        long before = jdbc.queryForObject("select count(*) from atlas_visits where not synthetic", Long.class);
        mvc.perform(post("/api/atlas-visits").cookie(new Cookie("atlas-consent", "v1.111"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"page\":\"HOME\"}"))
                .andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("select count(*) from atlas_visits where not synthetic", Long.class)).isEqualTo(before);
        assertThat(jdbc.queryForObject("select count(*) from atlas_visits where synthetic", Long.class)).isEqualTo(82);
        mvc.perform(get("/api/atlas-visits/report").param("from", "2026-11-01").param("to", "2026-11-14"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(0));
        mvc.perform(get("/api/atlas-visits/report").param("from", "2020-01-01").param("to", "2020-01-01"))
                .andExpect(jsonPath("$.total").value(0)).andExpect(jsonPath("$.days").isEmpty());
    }

    @Test
    void isolatedVisitCollectorRequiresCurrentAnalyticsConsent() throws Exception {
        var analytics = new com.smartcity.service.AtlasAnalyticsService(jdbc, true);
        var isolated = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(
                new com.smartcity.controller.AtlasAnalyticsController(analytics)).build();
        long before = jdbc.queryForObject("select count(*) from atlas_visits where not synthetic", Long.class);
        String endpoint = "/api/atlas-visits";
        String body = "{\"page\":\"CITY\"}";
        isolated.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        isolated.perform(post(endpoint).cookie(new Cookie("atlas-consent", "v1.101"))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        isolated.perform(post(endpoint).cookie(new Cookie("atlas-consent", "v1.010"))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isNoContent());
        isolated.perform(post(endpoint).cookie(new Cookie("atlas-consent", "v1.000"))
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        assertThat(jdbc.queryForObject("select count(*) from atlas_visits where not synthetic", Long.class))
                .isEqualTo(before + 1);
        assertThat(jdbc.queryForObject("select count(*) from atlas_visits where not synthetic and country is not null", Long.class))
                .isZero();
        mvc.perform(get("/api/atlas-visits/report")).andExpect(jsonPath("$.total").value(before + 1));
    }

    @Test
    void realStudentRegistrationSignInAndSignOutUseDatabaseAccounts() throws Exception {
        String email = "student-" + UUID.randomUUID() + "@example.test";
        String registration = """
                {"name":"Student Entered Name","email":"%s","password":"a long test password","student":true}
                """.formatted(email);
        var created = mvc.perform(post("/api/account/register").contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.student").value(true))
                .andExpect(jsonPath("$.admin").value(false)).andReturn();
        mvc.perform(get("/api/cvs/mine").session((MockHttpSession) created.getRequest().getSession(false)))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        String hash = jdbc.queryForObject("select password_hash from userses where email = ?", String.class, email);
        assertThat(hash).isNotBlank().doesNotContain("a long test password");
        String credentials = """
                {"email":"%s","password":"a long test password"}
                """.formatted(email);
        var signedIn = mvc.perform(post("/api/account/signin").contentType(MediaType.APPLICATION_JSON).content(credentials))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Student Entered Name")).andReturn();
        MockHttpSession session = (MockHttpSession) signedIn.getRequest().getSession(false);
        mvc.perform(post("/api/account/signout").session(session)).andExpect(status().isNoContent());
        mvc.perform(get("/api/account")).andExpect(status().isUnauthorized());
    }

    @Test
    void profileChangesAndPasswordRotationUseTheSignedInAccount() throws Exception {
        String original = "profile-" + UUID.randomUUID() + "@example.test";
        String updated = "update-" + UUID.randomUUID() + "@example.test";
        var registered = mvc.perform(post("/api/account/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Before","email":"%s","password":"original long password","student":true}
                """.formatted(original))).andExpect(status().isCreated()).andReturn();
        MockHttpSession session = (MockHttpSession) registered.getRequest().getSession(false);
        var signedIn = mvc.perform(post("/api/account/signin").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"%s","password":"original long password"}
                """.formatted(original))).andExpect(status().isOk()).andReturn();
        MockHttpSession otherSession = (MockHttpSession) signedIn.getRequest().getSession(false);
        mvc.perform(put("/api/account").session(session).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Updated name","email":"%s","student":false,"phone":"1234","birthday":"1990-01-01"}
                """.formatted(original))).andExpect(status().isOk()).andExpect(jsonPath("$.student").value(false));
        mvc.perform(get("/api/cvs/mine").session(session)).andExpect(status().isForbidden());
        mvc.perform(get("/api/account").session(session)).andExpect(jsonPath("$.phone").value("1234"))
                .andExpect(jsonPath("$.name").value("Updated name")).andExpect(jsonPath("$.birthday").value("1990-01-01"));
        mvc.perform(put("/api/account/password").session(session).contentType(MediaType.APPLICATION_JSON).content("""
                {"currentPassword":"wrong","newPassword":"replacement password"}
                """)).andExpect(status().isForbidden());
        var rotated = mvc.perform(put("/api/account/password").session(session).contentType(MediaType.APPLICATION_JSON).content("""
                {"currentPassword":"original long password","newPassword":"replacement password"}
                """)).andExpect(status().isOk()).andReturn();
        mvc.perform(get("/api/account").session(otherSession)).andExpect(status().isUnauthorized());
        session = (MockHttpSession) rotated.getRequest().getSession(false);
        var changed = mvc.perform(put("/api/account").session(session).contentType(MediaType.APPLICATION_JSON).content("""
                {"name":"Updated name","email":"%s","student":true,"currentPassword":"replacement password"}
                """.formatted(updated))).andExpect(status().isOk()).andExpect(jsonPath("$.student").value(true))
                .andExpect(jsonPath("$.email").value(updated)).andReturn();
        mvc.perform(get("/api/cvs/mine").session((MockHttpSession) changed.getRequest().getSession(false)))
                .andExpect(status().isOk());
        mvc.perform(post("/api/account/signin").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"%s","password":"original long password"}
                """.formatted(updated))).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/account/signin").contentType(MediaType.APPLICATION_JSON).content("""
                {"email":"%s","password":"replacement password"}
                """.formatted(updated))).andExpect(status().isOk());
    }

    @Test
    void auditHistorySupportsBoundedNonoverlappingPages() throws Exception {
        var complete = json.readTree(mvc.perform(get("/api/audit_history").param("limit", "4"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
        assertThat(complete.size()).isEqualTo(4);
        var next = json.readTree(mvc.perform(get("/api/audit_history").param("limit", "2").param("offset", "2"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
        assertThat(next.size()).isEqualTo(2);
        assertThat(next.get(0).path("id")).isEqualTo(complete.get(2).path("id"));
        assertThat(next.get(1).path("id")).isEqualTo(complete.get(3).path("id"));
        mvc.perform(get("/api/audit_history").param("query", "CITIES").param("operation", "INSERT").param("limit", "15"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].resource").value("cities"))
                .andExpect(jsonPath("$[0].operation").value("INSERT"));
        mvc.perform(get("/api/audit_history").param("query", "no-matching-activity-query").param("limit", "30"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/api/audit_history").param("limit", "101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/audit_history").param("offset", "-1")).andExpect(status().isBadRequest());
    }

    private MockHttpSession resident(boolean second) {
        Long id = jdbc.queryForObject("select id from userses where user_code = ?", Long.class,
                second ? "casablanca-demo-student" : "casablanca-demo-resident");
        var session = new MockHttpSession();
        session.setAttribute(com.smartcity.service.CurrentResident.SESSION_USER, id);
        return session;
    }

    private long idOf(MvcResult result, boolean first) throws Exception {
        JsonNode body = json.readTree(result.getResponse().getContentAsByteArray());
        return (first ? body.get(0) : body).required("id").longValue();
    }
}
