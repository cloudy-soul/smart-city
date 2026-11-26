package com.smartcity.service;

import com.smartcity.dto.CityDto;
import com.smartcity.entity.City;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CityRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CityService {

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CityDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CityDto findById(Long id) {
        return toDto(load(id));
    }

    public CityDto create(CityDto dto) {
        City e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public CityDto update(Long id, CityDto dto) {
        City e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private City load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("City " + id + " not found"));
    }

    private City toEntity(CityDto dto) {
        City e = new City(dto.name(), dto.country(), dto.description(), dto.latitude(), dto.longitude());
        apply(e, dto, false);
        return e;
    }

    private void apply(City e, CityDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setName(dto.name()); }
        if (includeConstructorFields) { e.setCountry(dto.country()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLatitude(dto.latitude()); }
        if (includeConstructorFields) { e.setLongitude(dto.longitude()); }
    }

    private CityDto toDto(City e) {
        return new CityDto(e.getId(),
                e.getName(),
                e.getCountry(),
                e.getDescription(),
                e.getLatitude(),
                e.getLongitude());
    }
}
