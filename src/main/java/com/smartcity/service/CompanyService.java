package com.smartcity.service;

import com.smartcity.dto.CompanyDto;
import com.smartcity.entity.Company;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CompanyRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CompanyDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CompanyDto findById(Long id) {
        return toDto(load(id));
    }

    public CompanyDto create(CompanyDto dto) {
        Company e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public CompanyDto update(Long id, CompanyDto dto) {
        Company e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Company load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Company " + id + " not found"));
    }

    private Company toEntity(CompanyDto dto) {
        Company e = new Company(dto.name(), dto.title(), dto.description(), dto.country(), dto.city(), dto.region(), dto.address(), dto.phone(), dto.email(), dto.tax(), dto.domainName());
        apply(e, dto, false);
        return e;
    }

    private void apply(Company e, CompanyDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setName(dto.name()); }
        if (includeConstructorFields) { e.setTitle(dto.title()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setCountry(dto.country()); }
        if (includeConstructorFields) { e.setCity(dto.city()); }
        if (includeConstructorFields) { e.setRegion(dto.region()); }
        if (includeConstructorFields) { e.setAddress(dto.address()); }
        if (includeConstructorFields) { e.setPhone(dto.phone()); }
        if (includeConstructorFields) { e.setEmail(dto.email()); }
        if (includeConstructorFields) { e.setTax(dto.tax()); }
        if (includeConstructorFields) { e.setDomainName(dto.domainName()); }
        if ((dto.latitude() == null) != (dto.longitude() == null)
                || (dto.latitude() != null && !(Math.abs(dto.latitude()) <= 90 && Math.abs(dto.longitude()) <= 180))) {
            throw new IllegalArgumentException("Provide both coordinates within latitude ±90 and longitude ±180.");
        }
        e.setLatitude(dto.latitude());
        e.setLongitude(dto.longitude());
    }

    private CompanyDto toDto(Company e) {
        return new CompanyDto(e.getId(),
                e.getName(),
                e.getTitle(),
                e.getDescription(),
                e.getCountry(),
                e.getCity(),
                e.getRegion(),
                e.getAddress(),
                e.getPhone(),
                e.getEmail(),
                e.getTax(),
                e.getDomainName(), e.getLatitude(), e.getLongitude());
    }
}
