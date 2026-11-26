package com.smartcity.service;

import com.smartcity.repository.UsersRepository;
import com.smartcity.security.JwtService;
import com.smartcity.security.PasswordEncoder;

/**
 * AuthService - generated from the UML diagram by diagram2app.
 */
public class AuthService {

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public UsersRepository getUsersRepository() {
        return usersRepository;
    }

    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public JwtService getJwtService() {
        return jwtService;
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void authenticate() {
        // TODO(diagram2app): the behaviour of AuthService.authenticate() is not specified in the diagram.
        throw new UnsupportedOperationException("AuthService.authenticate() is not implemented: behaviour not specified");
    }

    public void register() {
        // TODO(diagram2app): the behaviour of AuthService.register() is not specified in the diagram.
        throw new UnsupportedOperationException("AuthService.register() is not implemented: behaviour not specified");
    }

    public void resetPassword() {
        // TODO(diagram2app): the behaviour of AuthService.resetPassword() is not specified in the diagram.
        throw new UnsupportedOperationException("AuthService.resetPassword() is not implemented: behaviour not specified");
    }

    public void logout() {
        // TODO(diagram2app): the behaviour of AuthService.logout() is not specified in the diagram.
        throw new UnsupportedOperationException("AuthService.logout() is not implemented: behaviour not specified");
    }
}
