package com.smartcity.entity;

import com.smartcity.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

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
        // TODO(diagram2app): the behaviour of Booking.confirmBooking() is not specified in the diagram.
        throw new UnsupportedOperationException("Booking.confirmBooking() is not implemented: behaviour not specified");
    }

    public void cancelBooking() {
        // TODO(diagram2app): the behaviour of Booking.cancelBooking() is not specified in the diagram.
        throw new UnsupportedOperationException("Booking.cancelBooking() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "Booking{" + "id=" + getId() + ", " + "bookingDate=" + getBookingDate() + "}";
    }
}
