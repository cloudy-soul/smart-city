package com.smartcity.controller;

import com.smartcity.service.AtlasAnalyticsService;
import com.smartcity.service.AtlasAnalyticsService.Location;
import com.smartcity.service.AtlasAnalyticsService.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/atlas-visits")
public class AtlasAnalyticsController {

    public record CollectionSettings(boolean enabled) {
    }

    public record Visit(@NotNull Page page) {
    }

    private final AtlasAnalyticsService analytics;

    public AtlasAnalyticsController(AtlasAnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/collection")
    public CollectionSettings collection() {
        return new CollectionSettings(analytics.collectionEnabled());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void visit(@Valid @RequestBody Visit visit,
                      @CookieValue(name = "atlas-consent", defaultValue = "") String consent) {
        analytics.record(visit.page(), consent);
    }

    @GetMapping("/report")
    public AtlasAnalyticsService.Report report(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "COUNTRY") Location location) {
        return analytics.report(from, to, location);
    }
}
