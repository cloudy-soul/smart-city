package com.smartcity.service;

import com.smartcity.dto.VenueSlotDto;
import com.smartcity.entity.Venue;
import com.smartcity.entity.VenueSlot;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.SportEventRepository;
import com.smartcity.repository.VenueRepository;
import com.smartcity.repository.VenueSlotRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class VenueSlotService {

    private final VenueSlotRepository slots;
    private final VenueRepository venues;
    private final SportEventRepository events;

    public VenueSlotService(VenueSlotRepository slots, VenueRepository venues, SportEventRepository events) {
        this.slots = slots;
        this.venues = venues;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public List<VenueSlotDto> findAll() {
        return slots.findAll().stream().map(this::toDto).toList();
    }

    public VenueSlotDto save(Long id, VenueSlotDto dto) {
        if (!dto.endsAt().isAfter(dto.startsAt())) {
            throw new IllegalArgumentException("The slot must end after it starts.");
        }
        Venue venue = venues.lockById(dto.venueId())
                .orElseThrow(() -> new NotFoundException("Venue not found."));
        VenueSlot slot = id == null ? new VenueSlot(venue, dto.startsAt(), dto.endsAt()) : lock(id);
        if (id != null && events.existsBySlotId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The slot is linked to an event.");
        }
        if (slots.overlaps(dto.venueId(), id == null ? 0L : id, dto.startsAt(), dto.endsAt())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The venue already has a slot during that time.");
        }
        slot.schedule(venue, dto.startsAt(), dto.endsAt());
        return toDto(slots.save(slot));
    }

    public void delete(Long id) {
        slots.delete(lock(id));
    }

    private VenueSlot lock(Long id) {
        return slots.lockById(id).orElseThrow(() -> new NotFoundException("Venue slot not found."));
    }

    private VenueSlotDto toDto(VenueSlot slot) {
        return new VenueSlotDto(slot.getId(), slot.getVenue().getId(), slot.getStartsAt(), slot.getEndsAt());
    }
}
