package com.smartcity.service;

import com.smartcity.dto.BookingDto;
import com.smartcity.entity.Booking;
import com.smartcity.enums.BookingStatus;
import com.smartcity.entity.Venue;
import com.smartcity.entity.SportEvent;
import com.smartcity.entity.Users;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.BookingRepository;
import com.smartcity.repository.VenueRepository;
import java.time.LocalDateTime;
import com.smartcity.repository.SportEventRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookingService {

    private final BookingRepository repository;
    private final VenueRepository venueRepository;

    private static final ZoneId CASABLANCA = ZoneId.of("Africa/Casablanca");
    private final SportEventRepository events;
    private final CurrentResident resident;

    public BookingService(BookingRepository repository, VenueRepository venueRepository,
                          SportEventRepository events, CurrentResident resident) {
        this.repository = repository;
        this.venueRepository = venueRepository;
        this.events = events;
        this.resident = resident;
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
        Booking e = loadLegacy(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(loadLegacy(id));
    }

    @Transactional(readOnly = true)
    public List<BookingDto> mine() {
        return repository.findByUserIdAndEventIsNotNullOrderByIdDesc(resident.require().getId())
                .stream().map(this::toDto).toList();
    }

    public BookingDto reserve(Long eventId, int quantity) {
        Users user = resident.require();
        if (quantity < 1) {
            throw new IllegalArgumentException("Enter a positive ticket quantity.");
        }
        SportEvent event = lockEvent(eventId);
        LocalDateTime now = LocalDateTime.now(CASABLANCA);
        if (event.getSlot() == null || !event.getEventDate().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservations are closed for this event.");
        }
        if (quantity > event.availableTickets()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only " + event.availableTickets() + " tickets are available.");
        }
        event.reserveTickets(quantity);
        Booking booking = new Booking(now, BookingStatus.PENDING);
        booking.reserve(event, user, quantity, BigDecimal.valueOf(event.getTicketPrice()).setScale(2, RoundingMode.HALF_UP));
        return toDto(repository.save(booking));
    }

    public BookingDto cancel(Long id) {
        Long residentId = resident.require().getId();
        Booking booking = lockBooking(id);
        if (booking.getEvent() == null || !residentId.equals(booking.getUser().getId())) {
            throw new NotFoundException("Reservation not found.");
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            SportEvent event = lockEvent(booking.getEvent().getId());
            event.releaseTickets(booking.getQuantity());
            booking.cancelBooking();
        }
        return toDto(booking);
    }

    private SportEvent lockEvent(Long id) {
        return events.lockById(id).orElseThrow(() -> new NotFoundException("Sport event not found."));
    }

    private Booking lockBooking(Long id) {
        return repository.lockById(id).orElseThrow(() -> new NotFoundException("Booking not found."));
    }

    private Booking loadLegacy(Long id) {
        Booking booking = lockBooking(id);
        if (booking.getEvent() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Use the reservation cancellation endpoint.");
        }
        return booking;
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
                e.getVenue() == null ? null : e.getVenue().getId(),
                e.getEvent() == null ? null : e.getEvent().getId(),
                e.getUser() == null ? null : e.getUser().getId(),
                e.getQuantity(), e.getUnitPrice(), e.getStatus());
    }
}
