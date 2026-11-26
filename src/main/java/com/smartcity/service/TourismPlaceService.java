package com.smartcity.service;

import com.smartcity.dto.TourismPlaceDto;
import com.smartcity.entity.City;
import com.smartcity.entity.TourismPlace;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CityRepository;
import com.smartcity.repository.TourismPlaceRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TourismPlaceService {

    private final TourismPlaceRepository repository;
    private final CityRepository cityRepository;

    public TourismPlaceService(TourismPlaceRepository repository, CityRepository cityRepository) {
        this.repository = repository;
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<TourismPlaceDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TourismPlaceDto findById(Long id) {
        return toDto(load(id));
    }

    public TourismPlaceDto create(TourismPlaceDto dto) {
        TourismPlace e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public TourismPlaceDto update(Long id, TourismPlaceDto dto) {
        TourismPlace e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private TourismPlace load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("TourismPlace " + id + " not found"));
    }

    private City findCity(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("City " + id + " does not exist"));
    }

    private TourismPlace toEntity(TourismPlaceDto dto) {
        TourismPlace e = new TourismPlace(dto.name(), dto.description(), dto.latitude(), dto.longitude(), dto.address(), findCity(dto.cityId()), dto.category(), dto.openingHours(), dto.rating());
        apply(e, dto, false);
        return e;
    }

    private void apply(TourismPlace e, TourismPlaceDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setName(dto.name()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLatitude(dto.latitude()); }
        if (includeConstructorFields) { e.setLongitude(dto.longitude()); }
        if (includeConstructorFields) { e.setAddress(dto.address()); }
        if (includeConstructorFields) { e.setCity(dto.cityId() == null ? null : findCity(dto.cityId())); }
        if (includeConstructorFields) { e.setCategory(dto.category()); }
        if (includeConstructorFields) { e.setOpeningHours(dto.openingHours()); }
        if (includeConstructorFields) { e.setRating(dto.rating()); }
    }

    private TourismPlaceDto toDto(TourismPlace e) {
        return new TourismPlaceDto(e.getId(),
                e.getName(),
                e.getDescription(),
                e.getLatitude(),
                e.getLongitude(),
                e.getAddress(),
                e.getCity() == null ? null : e.getCity().getId(),
                e.getCategory(),
                e.getOpeningHours(),
                e.getRating());
    }
}
