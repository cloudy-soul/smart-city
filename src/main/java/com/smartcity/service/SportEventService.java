package com.smartcity.service;

import com.smartcity.dto.SportEventDto;
import com.smartcity.entity.SportEvent;
import com.smartcity.entity.Venue;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.SportEventRepository;
import com.smartcity.repository.VenueRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SportEventService {

    private final SportEventRepository repository;
    private final VenueRepository venueRepository;

    public SportEventService(SportEventRepository repository, VenueRepository venueRepository) {
        this.repository = repository;
        this.venueRepository = venueRepository;
    }

    @Transactional(readOnly = true)
    public List<SportEventDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SportEventDto findById(Long id) {
        return toDto(load(id));
    }

    public SportEventDto create(SportEventDto dto) {
        SportEvent e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public SportEventDto update(Long id, SportEventDto dto) {
        SportEvent e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private SportEvent load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("SportEvent " + id + " not found"));
    }

    private Venue findVenue(Long id) {
        return venueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Venue " + id + " does not exist"));
    }

    private SportEvent toEntity(SportEventDto dto) {
        SportEvent e = new SportEvent(dto.title(), dto.description(), dto.eventDate(), dto.ticketPrice(), findVenue(dto.venueId()));
        apply(e, dto, false);
        return e;
    }

    private void apply(SportEvent e, SportEventDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setTitle(dto.title()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setEventDate(dto.eventDate()); }
        if (includeConstructorFields) { e.setTicketPrice(dto.ticketPrice()); }
        if (includeConstructorFields) { e.setVenue(dto.venueId() == null ? null : findVenue(dto.venueId())); }
    }

    private SportEventDto toDto(SportEvent e) {
        return new SportEventDto(e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getEventDate(),
                e.getTicketPrice(),
                e.getVenue() == null ? null : e.getVenue().getId());
    }
}
