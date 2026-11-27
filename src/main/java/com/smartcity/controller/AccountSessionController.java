package com.smartcity.controller;

import com.smartcity.entity.Account;
import com.smartcity.entity.Students;
import com.smartcity.entity.Users;
import com.smartcity.repository.StudentsRepository;
import com.smartcity.repository.UsersRepository;
import com.smartcity.service.CurrentResident;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/account")
public class AccountSessionController {

    public record Credentials(@NotBlank @Email @Size(max = 255) String email,
                              @NotBlank @Size(max = 128) String password) {}
    public record Registration(@NotBlank @Size(max = 120) String name, @NotBlank @Email @Size(max = 255) String email,
                               @NotBlank @Size(min = 12, max = 128) String password, boolean student) {}
    public record Profile(String name, String email, boolean student, boolean admin,
                          String phone, String country, String city, String region, String address,
                          String nationality, LocalDate birthday, String zipcode) {}
    public record ProfileEdit(@NotBlank @Size(max = 120) String name, @NotBlank @Email @Size(max = 255) String email,
                              boolean student, @Size(max = 255) String phone, @Size(max = 255) String country,
                              @Size(max = 255) String city, @Size(max = 255) String region,
                              @Size(max = 255) String address, @Size(max = 255) String nationality,
                              @Past LocalDate birthday, @Size(max = 255) String zipcode,
                              @Size(max = 128) String currentPassword) {}
    public record PasswordChange(@NotBlank @Size(max = 128) String currentPassword,
                                 @NotBlank @Size(min = 12, max = 128) String newPassword) {}

    private final CurrentResident resident;
    private final UsersRepository users;
    private final StudentsRepository students;
    private final HttpServletRequest request;
    private final JdbcTemplate jdbc;
    private final jakarta.persistence.EntityManager entityManager;

    public AccountSessionController(CurrentResident resident, UsersRepository users, StudentsRepository students,
                                    HttpServletRequest request, JdbcTemplate jdbc, jakarta.persistence.EntityManager entityManager) {
        this.resident = resident;
        this.users = users;
        this.students = students;
        this.request = request;
        this.jdbc = jdbc;
        this.entityManager = entityManager;
    }

    @GetMapping
    public Profile account() { return profile(resident.require()); }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Profile register(@Valid @RequestBody Registration body) {
        String email = body.email().strip().toLowerCase(Locale.ROOT);
        String token = UUID.randomUUID().toString();
        Users user = new Users(body.name().strip(), "", email, "", "", "", "", "", "", null, "", "", token,
                new Account(email, encode(body.password())));
        setStudent(user, body.student());
        return session(users.saveAndFlush(user));
    }

    @PostMapping("/signin")
    public Profile signin(@Valid @RequestBody Credentials body) {
        Users user = users.findFirstByAccountUsername(body.email().strip().toLowerCase(Locale.ROOT)).orElse(null);
        String hash = user == null ? null : user.getAccount().getPasswordHash();
        if (hash == null || !matches(body.password(), hash)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        }
        return session(user);
    }

    @PutMapping
    @Transactional
    public Profile edit(@Valid @RequestBody ProfileEdit body) {
        Users user = lockAccount();
        String email = body.email().strip().toLowerCase(Locale.ROOT);
        boolean emailChanged = !email.equals(user.getEmail());
        if (emailChanged) {
            requirePassword(user, body.currentPassword());
            user.getAccount().setUsername(email);
            user.setEmail(email);
            user.invalidateCredentials();
        }
        if (!body.name().strip().equals(profile(user).name())) {
            user.setFirstname(body.name().strip());
            user.setLastname("");
        }
        user.setPhone(text(body.phone()));
        user.setCountry(text(body.country()));
        user.setCity(text(body.city()));
        user.setRegion(text(body.region()));
        user.setAddress(text(body.address()));
        user.setNationality(text(body.nationality()));
        user.setBirthday(body.birthday());
        user.setZipcode(text(body.zipcode()));
        setStudent(user, body.student());
        users.flush();
        return emailChanged ? session(user) : profile(user);
    }

    @PutMapping("/password")
    @Transactional
    public Profile changePassword(@Valid @RequestBody PasswordChange body) {
        Users user = lockAccount();
        requirePassword(user, body.currentPassword());
        user.getAccount().setPasswordHash(encode(body.newPassword()));
        user.invalidateCredentials();
        users.flush();
        return session(user);
    }

    private Users lockAccount() {
        Users current = resident.require();
        int version = current.getCredentialVersion();
        Users locked = users.lockById(current.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        // The account may already be in Hibernate's persistence context.
        entityManager.refresh(locked);
        if (version != locked.getCredentialVersion()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in again.");
        }
        return locked;
    }

    private void requirePassword(Users user, String supplied) {
        if (supplied == null || user.getAccount() == null || user.getAccount().getPasswordHash() == null
                || !matches(supplied, user.getAccount().getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The current password is incorrect.");
        }
    }

    private void setStudent(Users user, boolean active) {
        if (active && user.getStudents() == null) {
            var id = jdbc.queryForObject("select nextval('atlas_student_number')", Long.class);
            user.setStudents(students.save(new Students(id, null)));
        }
        user.setStudentActive(active);
    }

    private String text(String value) {
        return value == null ? "" : value.strip();
    }

    private String encode(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(derive(password, salt));
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signout() {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    private Profile session(Users user) {
        var old = request.getSession(false);
        if (old != null) old.invalidate();
        var session = request.getSession(true);
        session.setAttribute(CurrentResident.SESSION_USER, user.getId());
        session.setAttribute(CurrentResident.SESSION_CREDENTIAL_VERSION, user.getCredentialVersion());
        return profile(user);
    }

    private Profile profile(Users user) {
        return new Profile((user.getFirstname() + " " + user.getLastname()).strip(), user.getEmail(),
                user.isActiveStudent(), user.getAdmin() != null, user.getPhone(), user.getCountry(),
                user.getCity(), user.getRegion(), user.getAddress(), user.getNationality(), user.getBirthday(), user.getZipcode());
    }

    private boolean matches(String password, String hash) {
        var fields = hash.split("\\$", -1);
        if (fields.length != 2) return false;
        try {
            return MessageDigest.isEqual(Base64.getDecoder().decode(fields[1]),
                    derive(password, Base64.getDecoder().decode(fields[0])));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private byte[] derive(String password, byte[] salt) {
        var spec = new PBEKeySpec(password.toCharArray(), salt, 600000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (java.security.GeneralSecurityException ex) {
            throw new IllegalStateException("Password hashing unavailable", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
