package com.smartcity.service;

import com.smartcity.dto.AdminDto;
import com.smartcity.entity.Admin;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AdminDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AdminDto findById(Long id) {
        return toDto(load(id));
    }

    public AdminDto create(AdminDto dto) {
        Admin e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public AdminDto update(Long id, AdminDto dto) {
        Admin e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Admin load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Admin " + id + " not found"));
    }

    private Admin toEntity(AdminDto dto) {
        Admin e = new Admin(dto.authorization_level(), dto.adminID());
        apply(e, dto, false);
        return e;
    }

    private void apply(Admin e, AdminDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setAuthorization_level(dto.authorization_level()); }
        if (includeConstructorFields) { e.setAdminID(dto.adminID()); }
    }

    private AdminDto toDto(Admin e) {
        return new AdminDto(e.getId(),
                e.getAuthorization_level(),
                e.getAdminID());
    }
}
