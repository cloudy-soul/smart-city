package com.smartcity.service;

import com.smartcity.dto.CVDto;
import com.smartcity.entity.CV;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CVRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CVService {

    private final CVRepository repository;

    public CVService(CVRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CVDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CVDto findById(Long id) {
        return toDto(load(id));
    }

    public CVDto create(CVDto dto) {
        CV e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public CVDto update(Long id, CVDto dto) {
        CV e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private CV load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("CV " + id + " not found"));
    }

    private CV toEntity(CVDto dto) {
        CV e = new CV(dto.title(), dto.description(), dto.lastUpdate(), dto.fileType(), dto.storageServer(), dto.storagePath(), dto.fileName(), dto.fileExtension());
        apply(e, dto, false);
        return e;
    }

    private void apply(CV e, CVDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setTitle(dto.title()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLastUpdate(dto.lastUpdate()); }
        if (includeConstructorFields) { e.setFileType(dto.fileType()); }
        if (includeConstructorFields) { e.setStorageServer(dto.storageServer()); }
        if (includeConstructorFields) { e.setStoragePath(dto.storagePath()); }
        if (includeConstructorFields) { e.setFileName(dto.fileName()); }
        if (includeConstructorFields) { e.setFileExtension(dto.fileExtension()); }
    }

    private CVDto toDto(CV e) {
        return new CVDto(e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getLastUpdate(),
                e.getFileType(),
                e.getStorageServer(),
                e.getStoragePath(),
                e.getFileName(),
                e.getFileExtension());
    }
}
