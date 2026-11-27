package com.smartcity.controller;

import com.smartcity.dto.VenueSlotDto;
import com.smartcity.service.VenueSlotService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venue_slots")
public class VenueSlotController {

    private final VenueSlotService service;

    public VenueSlotController(VenueSlotService service) {
        this.service = service;
    }

    @GetMapping
    public List<VenueSlotDto> list() { return service.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueSlotDto create(@Valid @RequestBody VenueSlotDto dto) { return service.save(null, dto); }

    @PutMapping("/{id}")
    public VenueSlotDto update(@PathVariable Long id, @Valid @RequestBody VenueSlotDto dto) {
        return service.save(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
