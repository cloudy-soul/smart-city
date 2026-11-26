package com.smartcity.security;

import com.smartcity.enums.Role;
import com.smartcity.repository.BookingRepository;
import com.smartcity.repository.UsersRepository;

/**
 * AccessControlService - generated from the UML diagram by diagram2app.
 */
public class AccessControlService {

    private Role role;
    private BookingRepository bookingRepository;
    private UsersRepository usersRepository;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public UsersRepository getUsersRepository() {
        return usersRepository;
    }

    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void checkPermission() {
        // TODO(diagram2app): the behaviour of AccessControlService.checkPermission() is not specified in the diagram.
        throw new UnsupportedOperationException("AccessControlService.checkPermission() is not implemented: behaviour not specified");
    }

    public void checkRole() {
        // TODO(diagram2app): the behaviour of AccessControlService.checkRole() is not specified in the diagram.
        throw new UnsupportedOperationException("AccessControlService.checkRole() is not implemented: behaviour not specified");
    }

    public void checkOwnership() {
        // TODO(diagram2app): the behaviour of AccessControlService.checkOwnership() is not specified in the diagram.
        throw new UnsupportedOperationException("AccessControlService.checkOwnership() is not implemented: behaviour not specified");
    }
}
