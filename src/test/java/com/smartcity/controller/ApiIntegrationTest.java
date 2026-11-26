package com.smartcity.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder b) {
        return b;
    }

    private long create(String uri, String json) throws Exception {
        String body = mvc.perform(withAuth(post(uri)).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("id").asLong();
    }

    private String usersJson() throws Exception {
            return "{\"firstname\":\"sample\",\"lastname\":\"sample\",\"email\":\"user@example.com\",\"phone\":\"sample\",\"country\":\"sample\",\"city\":\"sample\",\"region\":\"sample\",\"address\":\"sample\",\"nationality\":\"sample\",\"birthday\":\"2024-01-15\",\"zipcode\":\"sample\",\"nationalID\":\"sample\",\"userCode\":\"sample\"}";
        }
    private String cVJson() {
    return """
        {
          "title": "Student CV",
          "description": "Sample CV for integration testing",
          "lastUpdate": "2024-01-15T10:30:00Z",
          "fileType": "application/pdf",
          "storageServer": "local",
          "storagePath": "/cvs/student-cv.pdf",
          "fileName": "student-cv.pdf",
          "fileExtension": "pdf"
        }
        """;
}

    private String studentsJson() throws Exception {
            long CVIdValue = create("/api/cvs", cVJson());
            return "{\"studentID\":1,\"CVId\":" + CVIdValue + "}";
        }

    private String adminJson() throws Exception {
            return "{\"authorization_level\":1,\"adminID\":1}";
        }

    private String touristsJson() throws Exception {
            return "{\"touristID\":1,\"preferredLanguage\":\"sample\",\"travelPurpose\":\"sample\"}";
        }

    private String companyJson() throws Exception {
            return "{\"name\":\"sample\",\"title\":\"sample\",\"description\":\"sample\",\"country\":\"sample\",\"city\":\"sample\",\"region\":\"sample\",\"address\":\"sample\",\"phone\":\"sample\",\"email\":\"sample\",\"tax\":\"sample\",\"domainName\":\"sample\"}";
        }

    private String cityJson() throws Exception {
            return "{\"name\":\"sample\",\"country\":\"sample\",\"description\":\"sample\",\"latitude\":1.0,\"longitude\":1.0}";
        }

    private String placeJson() throws Exception {
            long cityIdValue = create("/api/cities", cityJson());
            return "{\"name\":\"sample\",\"description\":\"sample\",\"latitude\":1.0,\"longitude\":1.0,\"address\":\"sample\",\"cityId\":" + cityIdValue + "}";
        }

    private String tourismPlaceJson() throws Exception {
            long cityIdValue = create("/api/cities", cityJson());
            return "{\"name\":\"sample\",\"description\":\"sample\",\"latitude\":1.0,\"longitude\":1.0,\"address\":\"sample\",\"cityId\":" + cityIdValue + ",\"category\":\"sample\",\"openingHours\":\"sample\",\"rating\":1.0}";
        }

    private String educationalInstitutionJson() throws Exception {
            long cityIdValue = create("/api/cities", cityJson());
            return "{\"name\":\"sample\",\"description\":\"sample\",\"latitude\":1.0,\"longitude\":1.0,\"address\":\"sample\",\"cityId\":" + cityIdValue + ",\"institutionType\":\"sample\",\"website\":\"sample\",\"contactEmail\":\"sample\"}";
        }

    private String venueJson() throws Exception {
            long cityIdValue = create("/api/cities", cityJson());
            return "{\"name\":\"sample\",\"description\":\"sample\",\"latitude\":1.0,\"longitude\":1.0,\"address\":\"sample\",\"cityId\":" + cityIdValue + ",\"venueType\":\"sample\",\"capacity\":1}";
        }

    private String sportEventJson() throws Exception {
            long venueIdValue = create("/api/venues", venueJson());
            return "{\"title\":\"sample\",\"description\":\"sample\",\"eventDate\":\"2024-01-15T10:30:00\",\"ticketPrice\":1.0,\"venueId\":" + venueIdValue + "}";
        }

    private String jobJson() throws Exception {
            return "{\"title\":\"sample\",\"description\":\"sample\",\"industry\":\"sample\",\"employmentType\":\"sample\",\"salary\":1.0}";
        }

    private String bookingJson() throws Exception {
            return "{\"bookingDate\":\"2024-01-15T10:30:00\"}";
        }

    @Test
    void usersCrudRoundTrip() throws Exception {
        String json = usersJson();
        long id = create("/api/userses", json);
        mvc.perform(withAuth(get("/api/userses/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/userses"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/userses/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/userses/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/userses/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void usersUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/userses/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void studentsCrudRoundTrip() throws Exception {
        String json = studentsJson();
        long id = create("/api/studentses", json);
        mvc.perform(withAuth(get("/api/studentses/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/studentses"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/studentses/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/studentses/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/studentses/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void studentsUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/studentses/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void adminCrudRoundTrip() throws Exception {
        String json = adminJson();
        long id = create("/api/admins", json);
        mvc.perform(withAuth(get("/api/admins/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/admins"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/admins/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/admins/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/admins/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void adminUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/admins/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void touristsCrudRoundTrip() throws Exception {
        String json = touristsJson();
        long id = create("/api/touristses", json);
        mvc.perform(withAuth(get("/api/touristses/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/touristses"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/touristses/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/touristses/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/touristses/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void touristsUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/touristses/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void companyCrudRoundTrip() throws Exception {
        String json = companyJson();
        long id = create("/api/companies", json);
        mvc.perform(withAuth(get("/api/companies/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/companies"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/companies/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/companies/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/companies/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void companyUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/companies/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void cityCrudRoundTrip() throws Exception {
        String json = cityJson();
        long id = create("/api/cities", json);
        mvc.perform(withAuth(get("/api/cities/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/cities"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/cities/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/cities/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/cities/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void cityUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/cities/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void placeCrudRoundTrip() throws Exception {
        // Place is abstract: create through a concrete subtype, then read/update/delete through /api/places.
        String json = placeJson();
        long id = create("/api/tourism_places", tourismPlaceJson());
        mvc.perform(withAuth(get("/api/places/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/places"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/places/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/places/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/places/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void placeUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/places/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void tourismPlaceCrudRoundTrip() throws Exception {
        String json = tourismPlaceJson();
        long id = create("/api/tourism_places", json);
        mvc.perform(withAuth(get("/api/tourism_places/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/tourism_places"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/tourism_places/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/tourism_places/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/tourism_places/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void tourismPlaceUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/tourism_places/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void educationalInstitutionCrudRoundTrip() throws Exception {
        String json = educationalInstitutionJson();
        long id = create("/api/educational_institutions", json);
        mvc.perform(withAuth(get("/api/educational_institutions/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/educational_institutions"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/educational_institutions/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/educational_institutions/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/educational_institutions/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void educationalInstitutionUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/educational_institutions/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void venueCrudRoundTrip() throws Exception {
        String json = venueJson();
        long id = create("/api/venues", json);
        mvc.perform(withAuth(get("/api/venues/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/venues"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/venues/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/venues/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/venues/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void venueUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/venues/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void sportEventCrudRoundTrip() throws Exception {
        String json = sportEventJson();
        long id = create("/api/sport_events", json);
        mvc.perform(withAuth(get("/api/sport_events/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/sport_events"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/sport_events/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/sport_events/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/sport_events/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void sportEventUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/sport_events/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void jobCrudRoundTrip() throws Exception {
        String json = jobJson();
        long id = create("/api/jobs", json);
        mvc.perform(withAuth(get("/api/jobs/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/jobs"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/jobs/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/jobs/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/jobs/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void jobUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/jobs/987654321"))).andExpect(status().isNotFound());
    }

    @Test
    void bookingCrudRoundTrip() throws Exception {
        String json = bookingJson();
        long id = create("/api/bookings", json);
        mvc.perform(withAuth(get("/api/bookings/" + id))).andExpect(status().isOk());
        mvc.perform(withAuth(get("/api/bookings"))).andExpect(status().isOk());
        mvc.perform(withAuth(put("/api/bookings/" + id)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(withAuth(delete("/api/bookings/" + id))).andExpect(status().isNoContent());
        mvc.perform(withAuth(get("/api/bookings/" + id))).andExpect(status().isNotFound());
    }

    @Test
    void bookingUnknownIdIsNotFound() throws Exception {
        mvc.perform(withAuth(get("/api/bookings/987654321"))).andExpect(status().isNotFound());
    }
}
