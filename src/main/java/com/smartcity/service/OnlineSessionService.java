package com.smartcity.service;

import com.smartcity.dto.OnlineSessionDto;
import com.smartcity.entity.OnlineSession;
import com.smartcity.entity.Token;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.OnlineSessionRepository;
import com.smartcity.repository.TokenRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OnlineSessionService {

    private final OnlineSessionRepository repository;
    private final TokenRepository tokenRepository;

    public OnlineSessionService(OnlineSessionRepository repository, TokenRepository tokenRepository) {
        this.repository = repository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional(readOnly = true)
    public List<OnlineSessionDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OnlineSessionDto findById(Long id) {
        return toDto(load(id));
    }

    public OnlineSessionDto create(OnlineSessionDto dto) {
        OnlineSession e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public OnlineSessionDto update(Long id, OnlineSessionDto dto) {
        OnlineSession e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private OnlineSession load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("OnlineSession " + id + " not found"));
    }

    private Token findToken(Long id) {
        return tokenRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Token " + id + " does not exist"));
    }

    private OnlineSession toEntity(OnlineSessionDto dto) {
        OnlineSession e = new OnlineSession(null, dto.timeStamp(), dto.authenticated(), findToken(dto.tokenId()));
        apply(e, dto, false);
        return e;
    }

    private void apply(OnlineSession e, OnlineSessionDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setTimeStamp(dto.timeStamp()); }
        if (includeConstructorFields) { e.setAuthenticated(dto.authenticated()); }
        if (includeConstructorFields) { e.setToken(dto.tokenId() == null ? null : findToken(dto.tokenId())); }
    }

    private OnlineSessionDto toDto(OnlineSession e) {
        return new OnlineSessionDto(e.getId(),
                e.getTimeStamp(),
                e.isAuthenticated(),
                e.getToken() == null ? null : e.getToken().getId());
    }
}
