package com.smartcity.controller;

import com.smartcity.dto.JobApplicationDto;
import com.smartcity.service.JobApplicationService;
import com.smartcity.enums.ApplicationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PatchMapping;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job_applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<JobApplicationDto> list() {
        return service.findAll();
    }

    public record Submission(@NotNull Long jobId, @NotNull Long cvId, @Size(max = 2000) String coverNote) {
    }

    public record StatusChange(@NotNull ApplicationStatus status) {
    }

    @GetMapping("/mine")
    public List<JobApplicationDto> mine() { return service.mine(); }

    @PostMapping("/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationDto submit(@Valid @RequestBody Submission submission) {
        return service.submit(submission.jobId(), submission.cvId(), submission.coverNote());
    }

    @PatchMapping("/{id}/status")
    public JobApplicationDto status(@PathVariable Long id, @Valid @RequestBody StatusChange change) {
        return service.updateStatus(id, change.status());
    }

    @GetMapping("/{id}")
    public JobApplicationDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationDto create(@Valid @RequestBody JobApplicationDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public JobApplicationDto update(@PathVariable Long id, @Valid @RequestBody JobApplicationDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
