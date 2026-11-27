package com.smartcity.controller;

import com.smartcity.dto.BookingDto;
import com.smartcity.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookingDto> list() {
        return service.findAll();
    }

    public record Reservation(@NotNull Long eventId, @NotNull @Positive Integer quantity) {
    }

    @GetMapping("/mine")
    public List<BookingDto> mine() { return service.mine(); }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto reserve(@Valid @RequestBody Reservation request) {
        return service.reserve(request.eventId(), request.quantity());
    }

    @PostMapping("/{id}/cancel")
    public BookingDto cancel(@PathVariable Long id) { return service.cancel(id); }

    @GetMapping("/{id}")
    public BookingDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@Valid @RequestBody BookingDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public BookingDto update(@PathVariable Long id, @Valid @RequestBody BookingDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
