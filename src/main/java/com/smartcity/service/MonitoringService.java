package com.smartcity.service;

import com.smartcity.dto.MonitoringDto;
import com.smartcity.entity.Monitoring;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.MonitoringRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MonitoringService {

    private final MonitoringRepository repository;

    public MonitoringService(MonitoringRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MonitoringDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MonitoringDto findById(Long id) {
        return toDto(load(id));
    }

    public MonitoringDto create(MonitoringDto dto) {
        Monitoring e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public MonitoringDto update(Long id, MonitoringDto dto) {
        Monitoring e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Monitoring load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Monitoring " + id + " not found"));
    }

    private Monitoring toEntity(MonitoringDto dto) {
        Monitoring e = new Monitoring(dto.recordType(), dto.source(), dto.status(), null, dto.timestamp());
        apply(e, dto, false);
        return e;
    }

    private void apply(Monitoring e, MonitoringDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setRecordType(dto.recordType()); }
        if (includeConstructorFields) { e.setSource(dto.source()); }
        if (includeConstructorFields) { e.setStatus(dto.status()); }
        if (includeConstructorFields) { e.setTimestamp(dto.timestamp()); }
    }

    private MonitoringDto toDto(Monitoring e) {
        return new MonitoringDto(e.getId(),
                e.getRecordType(),
                e.getSource(),
                e.getStatus(),
                e.getTimestamp());
    }
}
