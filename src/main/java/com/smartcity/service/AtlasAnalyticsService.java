package com.smartcity.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AtlasAnalyticsService {

    public enum Page { HOME, CITY, SEARCH, TOURISM, EDUCATION, SPORTS, JOBS, BUSINESS, PRIVACY, TERMS }
    public enum Location { COUNTRY, REGION, CITY }

    public record Count(String label, long views) {
    }

    public record Report(LocalDate from, LocalDate to, long total, List<Count> days,
                         List<Count> modules, List<Count> locations) {
    }

    private final JdbcTemplate jdbc;
    private final boolean enabled;

    public AtlasAnalyticsService(JdbcTemplate jdbc, @Value("${app.analytics.collection-enabled:false}") boolean enabled) {
        this.jdbc = jdbc;
        this.enabled = enabled;
    }

    public boolean collectionEnabled() {
        return enabled;
    }

    public void record(Page page, String consent) {
        if (!enabled) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!consent.matches("v1[.][01]1[01]")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Analytics consent is required.");
        }
        jdbc.update("INSERT INTO atlas_visits(page,synthetic) VALUES (?,false)", page.name());
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Report report(LocalDate from, LocalDate to, Location location) {
        if (from == null && to == null) {
            to = jdbc.queryForObject("SELECT max((visited_at AT TIME ZONE 'UTC')::date) FROM atlas_visits WHERE NOT synthetic",
                    LocalDate.class);
            if (to == null) to = LocalDate.now(ZoneOffset.UTC);
            from = to.minusDays(29);
        } else if (from == null || to == null) {
            throw new IllegalArgumentException("Provide both dates.");
        }
        if (from.isBefore(LocalDate.of(1900, 1, 1)) || to.isAfter(LocalDate.of(9999, 12, 30))
                || from.isAfter(to)) {
            throw new IllegalArgumentException("Choose a valid start and end date.");
        }
        var start = from.atStartOfDay().atOffset(ZoneOffset.UTC);
        var end = to.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        String scope = " FROM atlas_visits WHERE NOT synthetic AND visited_at >= ? AND visited_at < ?";
        String day = "(visited_at AT TIME ZONE 'UTC')::date";
        String area = switch (location) {
            case COUNTRY -> "coalesce(country,'Unknown')";
            case REGION -> "coalesce(region || ', ' || country,'Unknown')";
            case CITY -> "coalesce(approximate_city || ', ' || region || ', ' || country,'Unknown')";
        };
        List<Count> days = jdbc.query("SELECT " + day + ", count(*)" + scope + " GROUP BY 1 ORDER BY 1",
                (rs, row) -> new Count(rs.getString(1), rs.getLong(2)), start, end);
        List<Count> modules = jdbc.query("SELECT page, count(*)" + scope
                        + " AND page IN ('TOURISM','EDUCATION','SPORTS','JOBS','BUSINESS') GROUP BY 1 ORDER BY 2 DESC, 1",
                (rs, row) -> new Count(rs.getString(1), rs.getLong(2)), start, end);
        List<Count> locations = jdbc.query("SELECT " + area + ", count(*)" + scope + " GROUP BY 1 ORDER BY 2 DESC, 1",
                (rs, row) -> new Count(rs.getString(1), rs.getLong(2)), start, end);
        return new Report(from, to, days.stream().mapToLong(Count::views).sum(), days, modules, locations);
    }
}
