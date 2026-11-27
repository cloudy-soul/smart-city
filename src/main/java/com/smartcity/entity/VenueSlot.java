package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "venue_slots")
public class VenueSlot extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    protected VenueSlot() {
    }

    public VenueSlot(Venue venue, LocalDateTime startsAt, LocalDateTime endsAt) {
        schedule(venue, startsAt, endsAt);
    }

    public void schedule(Venue venue, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.venue = venue;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public Venue getVenue() { return venue; }
    public LocalDateTime getStartsAt() { return startsAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
}
