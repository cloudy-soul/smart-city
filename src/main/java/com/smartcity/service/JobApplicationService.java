package com.smartcity.service;

import com.smartcity.dto.JobApplicationDto;
import com.smartcity.entity.CV;
import com.smartcity.entity.Job;
import com.smartcity.entity.JobApplication;
import com.smartcity.enums.ApplicationStatus;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CVRepository;
import com.smartcity.repository.JobApplicationRepository;
import com.smartcity.repository.JobRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final JobRepository jobRepository;
    private final CVRepository cVRepository;

    private final CurrentResident resident;

    public JobApplicationService(JobApplicationRepository repository, JobRepository jobRepository,
                                 CVRepository cVRepository, CurrentResident resident) {
        this.repository = repository;
        this.jobRepository = jobRepository;
        this.cVRepository = cVRepository;
        this.resident = resident;
    }

    @Transactional(readOnly = true)
    public List<JobApplicationDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public JobApplicationDto findById(Long id) {
        return toDto(load(id));
    }

    public JobApplicationDto create(JobApplicationDto dto) {
        JobApplication e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public JobApplicationDto update(Long id, JobApplicationDto dto) {
        JobApplication e = loadLegacy(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(loadLegacy(id));
    }

    @Transactional(readOnly = true)
    public List<JobApplicationDto> mine() {
        return repository.findByUserIdAndSampleSubmissionTrueOrderByIdDesc(resident.require().getId())
                .stream().map(this::toDto).toList();
    }

    public JobApplicationDto submit(Long jobId, Long cvId, String note) {
        var user = resident.requireStudent();
        Job job = jobRepository.lockById(jobId).orElseThrow(() -> new NotFoundException("Job not found."));
        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline().isBefore(LocalDate.now(ZoneId.of("Africa/Casablanca")))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The application deadline has passed.");
        }
        CV cv = cVRepository.lockById(cvId).orElseThrow(() -> new NotFoundException("CV not found."));
        if (cv.getOwner() == null || !user.getId().equals(cv.getOwner().getId())) {
            throw new IllegalArgumentException("Choose a CV you uploaded.");
        }
        JobApplication application = new JobApplication(Instant.now(), ApplicationStatus.PENDING);
        application.setJob(job);
        application.setCV(cv);
        application.submit(user, note == null ? "" : note.strip());
        return toDto(repository.save(application));
    }

    public JobApplicationDto updateStatus(Long id, ApplicationStatus status) {
        JobApplication application = lock(id);
        if (!application.isSampleSubmission()) {
            throw new NotFoundException("Application not found.");
        }
        application.updateStatus(status);
        return toDto(application);
    }

    private JobApplication lock(Long id) {
        return repository.lockById(id).orElseThrow(() -> new NotFoundException("Application not found."));
    }

    private JobApplication loadLegacy(Long id) {
        JobApplication application = lock(id);
        if (application.isSampleSubmission()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only the status of a submitted application can be changed.");
        }
        return application;
    }

    private JobApplication load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("JobApplication " + id + " not found"));
    }

    private Job findJob(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Job " + id + " does not exist"));
    }

    private CV findCV(Long id) {
        return cVRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("CV " + id + " does not exist"));
    }

    private JobApplication toEntity(JobApplicationDto dto) {
        JobApplication e = new JobApplication(dto.applicationDate(), ApplicationStatus.PENDING);
        apply(e, dto, false);
        return e;
    }

    private void apply(JobApplication e, JobApplicationDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setApplicationDate(dto.applicationDate()); }
        e.setJob(dto.jobId() == null ? null : findJob(dto.jobId()));
        e.setCV(dto.cVId() == null ? null : findCV(dto.cVId()));
    }

    private JobApplicationDto toDto(JobApplication e) {
        return new JobApplicationDto(e.getId(),
                e.getApplicationDate(),
                e.getJob() == null ? null : e.getJob().getId(),
                e.getCV() == null ? null : e.getCV().getId(),
                e.getUser() == null ? null : e.getUser().getId(), e.getCoverNote(), e.getStatus(), e.isSampleSubmission());
    }
}
