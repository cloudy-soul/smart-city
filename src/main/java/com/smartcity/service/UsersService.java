package com.smartcity.service;

import com.smartcity.dto.UsersDto;
import com.smartcity.entity.Admin;
import com.smartcity.entity.Booking;
import com.smartcity.entity.JobApplication;
import com.smartcity.entity.OnlineSession;
import com.smartcity.entity.Students;
import com.smartcity.entity.Tourists;
import com.smartcity.entity.Users;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.AdminRepository;
import com.smartcity.repository.BookingRepository;
import com.smartcity.repository.JobApplicationRepository;
import com.smartcity.repository.OnlineSessionRepository;
import com.smartcity.repository.StudentsRepository;
import com.smartcity.repository.TouristsRepository;
import com.smartcity.repository.UsersRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsersService {

    private final UsersRepository repository;
    private final StudentsRepository studentsRepository;
    private final AdminRepository adminRepository;
    private final OnlineSessionRepository onlineSessionRepository;
    private final BookingRepository bookingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final TouristsRepository touristsRepository;

    public UsersService(UsersRepository repository, StudentsRepository studentsRepository, AdminRepository adminRepository, OnlineSessionRepository onlineSessionRepository, BookingRepository bookingRepository, JobApplicationRepository jobApplicationRepository, TouristsRepository touristsRepository) {
        this.repository = repository;
        this.studentsRepository = studentsRepository;
        this.adminRepository = adminRepository;
        this.onlineSessionRepository = onlineSessionRepository;
        this.bookingRepository = bookingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.touristsRepository = touristsRepository;
    }

    @Transactional(readOnly = true)
    public List<UsersDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UsersDto findById(Long id) {
        return toDto(load(id));
    }

    public UsersDto create(UsersDto dto) {
        Users e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public UsersDto update(Long id, UsersDto dto) {
        Users e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Users load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Users " + id + " not found"));
    }

    private Students findStudents(Long id) {
        return studentsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Students " + id + " does not exist"));
    }

    private Admin findAdmin(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Admin " + id + " does not exist"));
    }

    private OnlineSession findOnlineSession(Long id) {
        return onlineSessionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("OnlineSession " + id + " does not exist"));
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking " + id + " does not exist"));
    }

    private JobApplication findJobApplication(Long id) {
        return jobApplicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("JobApplication " + id + " does not exist"));
    }

    private Tourists findTourists(Long id) {
        return touristsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tourists " + id + " does not exist"));
    }

    private Users toEntity(UsersDto dto) {
        Users e = new Users(dto.firstname(), dto.lastname(), dto.email(), dto.phone(), dto.country(), dto.city(), dto.region(), dto.address(), dto.nationality(), dto.birthday(), dto.zipcode(), dto.nationalID(), dto.userCode(), null);
        apply(e, dto, false);
        return e;
    }

    private void apply(Users e, UsersDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setFirstname(dto.firstname()); }
        if (includeConstructorFields) { e.setLastname(dto.lastname()); }
        if (includeConstructorFields) { e.setEmail(dto.email()); }
        if (includeConstructorFields) { e.setPhone(dto.phone()); }
        if (includeConstructorFields) { e.setCountry(dto.country()); }
        if (includeConstructorFields) { e.setCity(dto.city()); }
        if (includeConstructorFields) { e.setRegion(dto.region()); }
        if (includeConstructorFields) { e.setAddress(dto.address()); }
        if (includeConstructorFields) { e.setNationality(dto.nationality()); }
        if (includeConstructorFields) { e.setBirthday(dto.birthday()); }
        if (includeConstructorFields) { e.setZipcode(dto.zipcode()); }
        if (includeConstructorFields) { e.setNationalID(dto.nationalID()); }
        if (includeConstructorFields) { e.setUserCode(dto.userCode()); }
        e.setStudents(dto.studentsId() == null ? null : findStudents(dto.studentsId()));
        e.setAdmin(dto.adminId() == null ? null : findAdmin(dto.adminId()));
        e.setOnlineSession(dto.onlineSessionId() == null ? null : findOnlineSession(dto.onlineSessionId()));
        if (dto.bookingsIds() != null) {
            e.getBookings().stream().filter(booking -> booking.getEvent() == null).toList().forEach(e::removeBooking);
            dto.bookingsIds().stream().map(this::findBooking)
                    .filter(booking -> booking.getEvent() == null).forEach(e::addBooking);
        }
        if (dto.jobApplicationsIds() != null) {
            e.getJobApplications().stream().filter(application -> !application.isSampleSubmission())
                    .toList().forEach(e::removeJobApplication);
            dto.jobApplicationsIds().stream().map(this::findJobApplication)
                    .filter(application -> !application.isSampleSubmission()).forEach(e::addJobApplication);
        }
        e.setTourists(dto.touristsId() == null ? null : findTourists(dto.touristsId()));
    }

    private UsersDto toDto(Users e) {
        return new UsersDto(e.getId(),
                e.getFirstname(),
                e.getLastname(),
                e.getEmail(),
                e.getPhone(),
                e.getCountry(),
                e.getCity(),
                e.getRegion(),
                e.getAddress(),
                e.getNationality(),
                e.getBirthday(),
                e.getZipcode(),
                e.getNationalID(),
                e.getUserCode(),
                e.getStudents() == null ? null : e.getStudents().getId(),
                e.getAdmin() == null ? null : e.getAdmin().getId(),
                e.getOnlineSession() == null ? null : e.getOnlineSession().getId(),
                e.getBookings().stream().map(Booking::getId).toList(),
                e.getJobApplications().stream().map(JobApplication::getId).toList(),
                e.getTourists() == null ? null : e.getTourists().getId());
    }
}
