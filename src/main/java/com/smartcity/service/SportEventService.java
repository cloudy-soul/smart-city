package com.smartcity.service;

import com.smartcity.dto.SportEventDto;
import com.smartcity.entity.SportEvent;
import com.smartcity.entity.Venue;
import com.smartcity.entity.VenueSlot;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.SportEventRepository;
import com.smartcity.repository.VenueRepository;
import com.smartcity.repository.VenueSlotRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SportEventService {

    private final SportEventRepository repository;
    private final VenueRepository venueRepository;

    private final VenueSlotRepository slots;

    public SportEventService(SportEventRepository repository, VenueRepository venueRepository, VenueSlotRepository slots) {
        this.repository = repository;
        this.venueRepository = venueRepository;
        this.slots = slots;
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
        SportEvent e = new SportEvent(dto.title(), dto.description(), null, dto.ticketPrice(), null);
        apply(e, dto);
        return toDto(repository.save(e));
    }

    public SportEventDto update(Long id, SportEventDto dto) {
        SportEvent e = lock(id);
        apply(e, dto);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(lock(id));
    }

    private SportEvent load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("SportEvent " + id + " not found"));
    }

    private SportEvent lock(Long id) {
        return repository.lockById(id).orElseThrow(() -> new NotFoundException("Sport event not found."));
    }

    private void apply(SportEvent event, SportEventDto dto) {
        if (!(dto.ticketPrice() >= 0 && dto.ticketPrice() <= 9999999999.99)) {
            throw new IllegalArgumentException("Enter a valid nonnegative ticket price.");
        }
        VenueSlot slot = dto.slotId() == null ? event.getSlot() : slots.lockById(dto.slotId())
                .orElseThrow(() -> new NotFoundException("Venue slot not found."));
        if (slot == null && (dto.eventDate() == null || dto.venueId() == null)) {
            throw new IllegalArgumentException("Choose a venue slot or provide a venue and event date.");
        }
        Venue venue = slot == null ? venueRepository.findById(dto.venueId())
                .orElseThrow(() -> new NotFoundException("Venue not found.")) : slot.getVenue();
        int capacity = dto.ticketCapacity() == null ? event.getTicketCapacity() : dto.ticketCapacity();
        if (capacity < event.getReservedTickets()
                || (slot == null && capacity != 0) || (slot != null && capacity > venue.getCapacity())) {
            throw new IllegalArgumentException("Ticket capacity must accommodate existing reservations and fit the scheduled venue.");
        }
        if (slot != null && BigDecimal.valueOf(dto.ticketPrice()).stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Enter a ticket price with at most two decimal places.");
        }
        if (event.getReservedTickets() != 0 && !event.getSlot().getId().equals(slot.getId())) {
            throw new IllegalArgumentException("Cancel active reservations before rescheduling the event.");
        }
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setTicketPrice(dto.ticketPrice());
        event.setVenue(venue);
        event.setEventDate(slot == null ? dto.eventDate() : slot.getStartsAt());
        event.setSlot(slot);
        event.setTicketCapacity(capacity);
    }

    private SportEventDto toDto(SportEvent event) {
        return new SportEventDto(event.getId(), event.getTitle(), event.getDescription(), event.getEventDate(),
                event.getTicketPrice(), event.getVenue().getId(),
                event.getSlot() == null ? null : event.getSlot().getId(),
                event.getTicketCapacity(), event.availableTickets());
    }
}
