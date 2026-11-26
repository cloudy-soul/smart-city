package com.smartcity.service;

import com.smartcity.dto.JobApplicationDto;
import com.smartcity.entity.CV;
import com.smartcity.entity.Job;
import com.smartcity.entity.JobApplication;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CVRepository;
import com.smartcity.repository.JobApplicationRepository;
import com.smartcity.repository.JobRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final JobRepository jobRepository;
    private final CVRepository cVRepository;

    public JobApplicationService(JobApplicationRepository repository, JobRepository jobRepository, CVRepository cVRepository) {
        this.repository = repository;
        this.jobRepository = jobRepository;
        this.cVRepository = cVRepository;
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
        JobApplication e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
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
        JobApplication e = new JobApplication(dto.applicationDate(), null);
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
                e.getCV() == null ? null : e.getCV().getId());
    }
}
