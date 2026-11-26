package com.smartcity.service;

import com.smartcity.dto.VenueDto;
import com.smartcity.entity.City;
import com.smartcity.entity.Venue;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CityRepository;
import com.smartcity.repository.VenueRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VenueService {

    private final VenueRepository repository;
    private final CityRepository cityRepository;

    public VenueService(VenueRepository repository, CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<VenueDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public VenueDto findById(Long id) {
        return toDto(load(id));
    }

    public VenueDto create(VenueDto dto) {
        Venue e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public VenueDto update(Long id, VenueDto dto) {
        Venue e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Venue load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Venue " + id + " not found"));
    }

    private City findCity(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("City " + id + " does not exist"));
    }

    private Venue toEntity(VenueDto dto) {
        Venue e = new Venue(dto.name(), dto.description(), dto.latitude(), dto.longitude(), dto.address(), findCity(dto.cityId()), dto.venueType(), dto.capacity());
        apply(e, dto, false);
        return e;
    }

    private void apply(Venue e, VenueDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setName(dto.name()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLatitude(dto.latitude()); }
        if (includeConstructorFields) { e.setLongitude(dto.longitude()); }
        if (includeConstructorFields) { e.setAddress(dto.address()); }
        if (includeConstructorFields) { e.setCity(dto.cityId() == null ? null : findCity(dto.cityId())); }
        if (includeConstructorFields) { e.setVenueType(dto.venueType()); }
        if (includeConstructorFields) { e.setCapacity(dto.capacity()); }
    }

    private VenueDto toDto(Venue e) {
        return new VenueDto(e.getId(),
                e.getName(),
                e.getDescription(),
                e.getLatitude(),
                e.getLongitude(),
                e.getAddress(),
                e.getCity() == null ? null : e.getCity().getId(),
                e.getVenueType(),
                e.getCapacity());
    }
}
