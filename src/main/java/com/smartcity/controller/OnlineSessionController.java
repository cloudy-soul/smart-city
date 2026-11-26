package com.smartcity.controller;

import com.smartcity.dto.OnlineSessionDto;
import com.smartcity.service.OnlineSessionService;
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
@RequestMapping("/api/online_sessions")
public class OnlineSessionController {

    private final OnlineSessionService service;

    public OnlineSessionController(OnlineSessionService service) {
        this.service = service;
    }

    @GetMapping
    public List<OnlineSessionDto> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public OnlineSessionDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OnlineSessionDto create(@Valid @RequestBody OnlineSessionDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public OnlineSessionDto update(@PathVariable Long id, @Valid @RequestBody OnlineSessionDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
