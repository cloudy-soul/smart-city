package com.smartcity.service;

import com.smartcity.dto.EducationalInstitutionDto;
import com.smartcity.entity.City;
import com.smartcity.entity.EducationalInstitution;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CityRepository;
import com.smartcity.repository.EducationalInstitutionRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EducationalInstitutionService {

    private final EducationalInstitutionRepository repository;
    private final CityRepository cityRepository;

    public EducationalInstitutionService(EducationalInstitutionRepository repository, CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<EducationalInstitutionDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public EducationalInstitutionDto findById(Long id) {
        return toDto(load(id));
    }

    public EducationalInstitutionDto create(EducationalInstitutionDto dto) {
        EducationalInstitution e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public EducationalInstitutionDto update(Long id, EducationalInstitutionDto dto) {
        EducationalInstitution e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private EducationalInstitution load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("EducationalInstitution " + id + " not found"));
    }

    private City findCity(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("City " + id + " does not exist"));
    }

    private EducationalInstitution toEntity(EducationalInstitutionDto dto) {
        EducationalInstitution e = new EducationalInstitution(dto.name(), dto.description(), dto.latitude(), dto.longitude(), dto.address(), findCity(dto.cityId()), dto.institutionType(), dto.website(), dto.contactEmail());
        apply(e, dto, false);
        return e;
    }

    private void apply(EducationalInstitution e, EducationalInstitutionDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setName(dto.name()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLatitude(dto.latitude()); }
        if (includeConstructorFields) { e.setLongitude(dto.longitude()); }
        if (includeConstructorFields) { e.setAddress(dto.address()); }
        if (includeConstructorFields) { e.setCity(dto.cityId() == null ? null : findCity(dto.cityId())); }
        if (includeConstructorFields) { e.setInstitutionType(dto.institutionType()); }
        if (includeConstructorFields) { e.setWebsite(dto.website()); }
        if (includeConstructorFields) { e.setContactEmail(dto.contactEmail()); }
    }

    private EducationalInstitutionDto toDto(EducationalInstitution e) {
        return new EducationalInstitutionDto(e.getId(),
                e.getName(),
                e.getDescription(),
                e.getLatitude(),
                e.getLongitude(),
                e.getAddress(),
                e.getCity() == null ? null : e.getCity().getId(),
                e.getInstitutionType(),
                e.getWebsite(),
                e.getContactEmail());
    }
}
