package com.smartcity.controller;

import com.smartcity.dto.TokenDto;
import com.smartcity.service.TokenService;
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
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenService service;

    public TokenController(TokenService service) {
        this.service = service;
    }

    @GetMapping
    public List<TokenDto> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TokenDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDto create(@Valid @RequestBody TokenDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public TokenDto update(@PathVariable Long id, @Valid @RequestBody TokenDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
