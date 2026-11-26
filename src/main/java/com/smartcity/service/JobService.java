package com.smartcity.service;

import com.smartcity.dto.JobDto;
import com.smartcity.entity.Company;
import com.smartcity.entity.Job;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CompanyRepository;
import com.smartcity.repository.JobRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobService {

    private final JobRepository repository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository repository, CompanyRepository companyRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<JobDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public JobDto findById(Long id) {
        return toDto(load(id));
    }

    public JobDto create(JobDto dto) {
        Job e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public JobDto update(Long id, JobDto dto) {
        Job e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Job load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Job " + id + " not found"));
    }

    private Company findCompany(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Company " + id + " does not exist"));
    }

    private Job toEntity(JobDto dto) {
        Job e = new Job(dto.title(), dto.description(), dto.industry(), dto.employmentType(), dto.salary());
        apply(e, dto, false);
        return e;
    }

    private void apply(Job e, JobDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setTitle(dto.title()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setIndustry(dto.industry()); }
        if (includeConstructorFields) { e.setEmploymentType(dto.employmentType()); }
        if (includeConstructorFields) { e.setSalary(dto.salary()); }
        e.setCompany(dto.companyId() == null ? null : findCompany(dto.companyId()));
    }

    private JobDto toDto(Job e) {
        return new JobDto(e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getIndustry(),
                e.getEmploymentType(),
                e.getSalary(),
                e.getCompany() == null ? null : e.getCompany().getId());
    }
}
