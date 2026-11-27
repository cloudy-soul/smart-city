package com.smartcity.controller;

import com.smartcity.dto.BusinessNewsDto;
import com.smartcity.service.BusinessNewsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business_news")
public class BusinessNewsController {

    private final BusinessNewsService service;

    public BusinessNewsController(BusinessNewsService service) { this.service = service; }

    @GetMapping
    public List<BusinessNewsDto> published() { return service.list(false); }

    @GetMapping("/admin")
    public List<BusinessNewsDto> all() { return service.list(true); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessNewsDto create(@Valid @RequestBody BusinessNewsDto dto) { return service.save(null, dto); }

    @PutMapping("/{id}")
    public BusinessNewsDto update(@PathVariable Long id, @Valid @RequestBody BusinessNewsDto dto) {
        return service.save(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
