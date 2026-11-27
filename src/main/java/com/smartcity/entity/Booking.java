package com.smartcity.entity;

import com.smartcity.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Booking - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime bookingDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private SportEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;

    @Column(nullable = false)
    private int quantity = 1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    public SportEvent getEvent() { return event; }
    public Users getUser() { return user; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUser(Users user) { this.user = user; }

    public void reserve(SportEvent event, Users user, int quantity, BigDecimal unitPrice) {
        this.event = event;
        this.venue = event.getVenue();
        this.user = user;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = BookingStatus.CONFIRMED;
    }

    protected Booking() {
    }

    public Booking(LocalDateTime bookingDate, BookingStatus status) {
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void confirmBooking() {
        if (status == BookingStatus.PENDING) {
            status = BookingStatus.CONFIRMED;
        }
    }

    public void cancelBooking() {
        status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Booking{" + "id=" + getId() + ", " + "bookingDate=" + getBookingDate() + "}";
    }
}
