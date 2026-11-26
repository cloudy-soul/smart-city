package com.smartcity.controller;

import com.smartcity.dto.MonitoringDto;
import com.smartcity.service.MonitoringService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitorings")
public class MonitoringController {

    private final MonitoringService service;

    public MonitoringController(MonitoringService service) {
        this.service = service;
    }

    @GetMapping
    public List<MonitoringDto> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MonitoringDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MonitoringDto create(@Valid @RequestBody MonitoringDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public MonitoringDto update(@PathVariable Long id, @Valid @RequestBody MonitoringDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
