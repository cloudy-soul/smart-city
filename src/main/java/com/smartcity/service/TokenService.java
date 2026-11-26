package com.smartcity.service;

import com.smartcity.dto.TokenDto;
import com.smartcity.entity.Token;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.TokenRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TokenService {

    private final TokenRepository repository;

    public TokenService(TokenRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TokenDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TokenDto findById(Long id) {
        return toDto(load(id));
    }

    public TokenDto create(TokenDto dto) {
        Token e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public TokenDto update(Long id, TokenDto dto) {
        Token e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Token load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Token " + id + " not found"));
    }

    private Token toEntity(TokenDto dto) {
        Token e = new Token(dto.creationDate(), dto.expirationDateTime(), dto.tokenValue());
        apply(e, dto, false);
        return e;
    }

    private void apply(Token e, TokenDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setCreationDate(dto.creationDate()); }
        if (includeConstructorFields) { e.setExpirationDateTime(dto.expirationDateTime()); }
        if (includeConstructorFields) { e.setTokenValue(dto.tokenValue()); }
    }

    private TokenDto toDto(Token e) {
        return new TokenDto(e.getId(),
                e.getCreationDate(),
                e.getExpirationDateTime(),
                e.getTokenValue());
    }
}
