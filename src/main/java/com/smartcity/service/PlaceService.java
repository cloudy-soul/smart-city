package com.smartcity.service;

import com.smartcity.dto.PlaceDto;
import com.smartcity.entity.City;
import com.smartcity.entity.EducationalInstitution;
import com.smartcity.entity.Place;
import com.smartcity.entity.TourismPlace;
import com.smartcity.entity.Venue;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CityRepository;
import com.smartcity.repository.EducationalInstitutionRepository;
import com.smartcity.repository.PlaceRepository;
import com.smartcity.repository.TourismPlaceRepository;
import com.smartcity.repository.VenueRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaceService {

    private final PlaceRepository repository;
    private final CityRepository cityRepository;
    private final TourismPlaceRepository tourismPlaceRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final VenueRepository venueRepository;

    public PlaceService(
            PlaceRepository repository,
            CityRepository cityRepository,
            TourismPlaceRepository tourismPlaceRepository,
            EducationalInstitutionRepository educationalInstitutionRepository,
            VenueRepository venueRepository) {

        this.repository = repository;
        this.cityRepository = cityRepository;
        this.tourismPlaceRepository = tourismPlaceRepository;
        this.educationalInstitutionRepository = educationalInstitutionRepository;
        this.venueRepository = venueRepository;
    }

    public TourismPlace createTourismPlace(TourismPlace place) {
        return tourismPlaceRepository.save(place);
    }

    public EducationalInstitution createEducationalInstitution(
            EducationalInstitution institution) {
        return educationalInstitutionRepository.save(institution);
    }

    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    @Transactional(readOnly = true)
    public List<PlaceDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PlaceDto findById(Long id) {
        return toDto(load(id));
    }

    public PlaceDto create(PlaceDto dto) {
        Place e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public PlaceDto update(Long id, PlaceDto dto) {
        Place e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Place load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Place " + id + " not found"));
    }

    private City findCity(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("City " + id + " does not exist"));
    }

    private Place toEntity(PlaceDto dto) {
        throw new IllegalArgumentException(
            "Place is abstract: create it through /api/tourism_places, /api/venues or /api/educational_institutions"
        );
    }

    private void apply(Place e, PlaceDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setName(dto.name()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLatitude(dto.latitude()); }
        if (includeConstructorFields) { e.setLongitude(dto.longitude()); }
        if (includeConstructorFields) { e.setAddress(dto.address()); }
        if (includeConstructorFields) { e.setCity(dto.cityId() == null ? null : findCity(dto.cityId())); }
    }

    private PlaceDto toDto(Place e) {
        return new PlaceDto(e.getId(),
                e.getName(),
                e.getDescription(),
                e.getLatitude(),
                e.getLongitude(),
                e.getAddress(),
                e.getCity() == null ? null : e.getCity().getId());
    }
}
