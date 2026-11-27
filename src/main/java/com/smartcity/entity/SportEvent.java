package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * SportEvent - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "sport_events")
public class SportEvent {

    @NotBlank
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime eventDate;
    private double ticketPrice;
    @ManyToOne(optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "slot_id", unique = true)
    private VenueSlot slot;

    @Column(nullable = false)
    private int ticketCapacity;

    @Column(nullable = false)
    private int reservedTickets;

    public VenueSlot getSlot() { return slot; }
    public int getTicketCapacity() { return ticketCapacity; }
    public int getReservedTickets() { return reservedTickets; }
    public int availableTickets() { return ticketCapacity - reservedTickets; }

    public void setSlot(VenueSlot slot) { this.slot = slot; }
    public void setTicketCapacity(int ticketCapacity) { this.ticketCapacity = ticketCapacity; }
    public void reserveTickets(int quantity) { reservedTickets += quantity; }
    public void releaseTickets(int quantity) { reservedTickets -= quantity; }

    protected SportEvent() {
    }

    public SportEvent(String title, String description, LocalDateTime eventDate, double ticketPrice, Venue venue) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.ticketPrice = ticketPrice;
        this.venue = venue;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    @Override
    public String toString() {
        return "SportEvent{" + "title=" + getTitle() + ", " + "description=" + getDescription() + ", " + "eventDate=" + getEventDate() + ", " + "ticketPrice=" + getTicketPrice() + "}";
    }
}
