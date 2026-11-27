package com.smartcity.controller;

import com.smartcity.service.CurrentResident;
import java.sql.Array;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PersonalHistoryController {

    private enum Target { TOURISM, EDUCATION, SPORTS, JOBS, BUSINESS, NEWS }

    public record Favourite(String module, long recordId) {
    }

    public record AuditEntry(long id, Instant date, String databaseActor, String workflowActor, String resource,
                             String recordKey, String operation, List<String> changedFields,
                             String previousStatus, String currentStatus, String beforeValues, String afterValues) {
    }

    private final JdbcTemplate jdbc;
    private final CurrentResident resident;

    public PersonalHistoryController(JdbcTemplate jdbc, CurrentResident resident) {
        this.jdbc = jdbc;
        this.resident = resident;
    }

    @GetMapping("/favourites")
    @Transactional(readOnly = true)
    public List<Favourite> favourites() {
        return jdbc.query("""
                select entry.key, entry.value from favourites f
                cross join lateral jsonb_each_text(to_jsonb(f) - 'user_id' - 'id') entry
                where f.user_id = ? and entry.value is not null order by f.id desc
                """, (row, index) -> new Favourite(row.getString(1).replaceFirst("_id$", ""), row.getLong(2)),
                resident.require().getId());
    }

    @PutMapping("/favourites/{module}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void saveFavourite(@PathVariable String module, @PathVariable long id) {
        String column = column(module);
        Long user = resident.require().getId();
        if (module.equalsIgnoreCase("news") && !Boolean.TRUE.equals(jdbc.queryForObject(
                "select exists(select 1 from business_news where id = ? and published_at is not null)", Boolean.class, id))) {
            throw new IllegalArgumentException("Choose a published news item.");
        }
        jdbc.update("insert into favourites(user_id, " + column + ") values (?, ?) on conflict do nothing", user, id);
    }

    @DeleteMapping("/favourites/{module}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void removeFavourite(@PathVariable String module, @PathVariable long id) {
        jdbc.update("delete from favourites where user_id = ? and " + column(module) + " = ?", resident.require().getId(), id);
    }

    private String column(String module) {
        return Target.valueOf(module.toUpperCase(Locale.ROOT)).name().toLowerCase(Locale.ROOT) + "_id";
    }

    @GetMapping("/audit_history")
    @Transactional(readOnly = true)
    public List<AuditEntry> audit(@RequestParam(defaultValue = "9223372036854775807") long before,
                                  @RequestParam(defaultValue = "0") long offset,
                                  @RequestParam(defaultValue = "100") int limit,
                                  @RequestParam(defaultValue = "") String query,
                                  @RequestParam(defaultValue = "") String operation) {
        if (offset < 0 || limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Use a nonnegative offset and a limit between 1 and 100.");
        }
        return jdbc.query("""
                select * from audit_history
                where id < ?
                  and (? = '' or operation = ?)
                  and (? = '' or strpos(lower(concat_ws(' ', resource, record_key, operation, workflow_actor,
                    database_actor, recorded_at, changed_fields::text)), lower(?)) > 0)
                order by id desc limit ? offset ?
                """, (row, index) -> {
            Array fields = row.getArray("changed_fields");
            try {
                return new AuditEntry(row.getLong("id"), row.getObject("recorded_at", OffsetDateTime.class).toInstant(),
                        row.getString("database_actor"), row.getString("workflow_actor"), row.getString("resource"),
                        row.getString("record_key"), row.getString("operation"), List.of((String[]) fields.getArray()),
                        row.getString("previous_status"), row.getString("current_status"),
                        row.getString("before_values"), row.getString("after_values"));
            } finally {
                fields.free();
            }
        }, before, operation, operation, query, query, limit, offset);
    }
}
