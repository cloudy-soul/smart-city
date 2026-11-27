package com.smartcity.service;

import com.smartcity.entity.Users;
import com.smartcity.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentResident {

    public static final String SESSION_USER = "atlas.user";
    public static final String SESSION_CREDENTIAL_VERSION = "atlas.credentialVersion";
    private final HttpServletRequest request;
    private final UsersRepository users;
    private final JdbcTemplate jdbc;

    public CurrentResident(HttpServletRequest request, UsersRepository users, JdbcTemplate jdbc) {
        this.request = request;
        this.users = users;
        this.jdbc = jdbc;
    }

    public Users find() {
        var session = request.getSession(false);
        if (session == null || !(session.getAttribute(SESSION_USER) instanceof Long id)) return null;
        int version = session.getAttribute(SESSION_CREDENTIAL_VERSION) instanceof Integer value ? value : 0;
        return users.findById(id).filter(user -> user.getCredentialVersion() == version).orElse(null);
    }

    public Users require() {
        Users user = find();
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in to continue.");
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            jdbc.queryForObject("select set_config('smartcity.workflow_actor', ?, true)", String.class, "account:" + user.getId());
        }
        return user;
    }

    public Users requireStudent() {
        Users user = require();
        if (!user.isActiveStudent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A student account is required to manage CVs or apply.");
        }
        return user;
    }
}
