package com.smartcity.service;

import com.smartcity.dto.BookingDto;
import com.smartcity.entity.Booking;
import com.smartcity.enums.BookingStatus;
import com.smartcity.entity.Venue;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.BookingRepository;
import com.smartcity.repository.VenueRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookingService {

    private final BookingRepository repository;
    private final VenueRepository venueRepository;

    public BookingService(BookingRepository repository, VenueRepository venueRepository) {
        this.repository = repository;
        this.venueRepository = venueRepository;
    }

    @Transactional(readOnly = true)
    public List<BookingDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BookingDto findById(Long id) {
        return toDto(load(id));
    }

    public BookingDto create(BookingDto dto) {
        Booking e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public BookingDto update(Long id, BookingDto dto) {
        Booking e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Booking load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Booking " + id + " not found"));
    }

    private Venue findVenue(Long id) {
        return venueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Venue " + id + " does not exist"));
    }

    private Booking toEntity(BookingDto dto) {
        Booking e = new Booking(dto.bookingDate(), BookingStatus.PENDING);
        apply(e, dto, false);
        return e;
    }

    private void apply(Booking e, BookingDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setBookingDate(dto.bookingDate()); }
        e.setVenue(dto.venueId() == null ? null : findVenue(dto.venueId()));
    }

    private BookingDto toDto(Booking e) {
        return new BookingDto(e.getId(),
                e.getBookingDate(),
                e.getVenue() == null ? null : e.getVenue().getId());
    }
}
