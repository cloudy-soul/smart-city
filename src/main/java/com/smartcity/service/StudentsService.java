package com.smartcity.service;

import com.smartcity.dto.StudentsDto;
import com.smartcity.entity.CV;
import com.smartcity.entity.EducationalInstitution;
import com.smartcity.entity.Students;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CVRepository;
import com.smartcity.repository.EducationalInstitutionRepository;
import com.smartcity.repository.StudentsRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentsService {

    private final StudentsRepository repository;
    private final CVRepository cVRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;

    public StudentsService(StudentsRepository repository, CVRepository cVRepository, EducationalInstitutionRepository educationalInstitutionRepository) {
        this.repository = repository;
        this.cVRepository = cVRepository;
        this.educationalInstitutionRepository = educationalInstitutionRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentsDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StudentsDto findById(Long id) {
        return toDto(load(id));
    }

    public StudentsDto create(StudentsDto dto) {
        Students e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public StudentsDto update(Long id, StudentsDto dto) {
        Students e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Students load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Students " + id + " not found"));
    }

    private CV findCV(Long id) {
        return cVRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("CV " + id + " does not exist"));
    }

    private EducationalInstitution findEducationalInstitution(Long id) {
        return educationalInstitutionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("EducationalInstitution " + id + " does not exist"));
    }

    private Students toEntity(StudentsDto dto) {
        Students e = new Students(dto.studentID(), findCV(dto.CVId()));
        apply(e, dto, false);
        return e;
    }

    private void apply(Students e, StudentsDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setStudentID(dto.studentID()); }
        if (includeConstructorFields) { e.setCV(dto.CVId() == null ? null : findCV(dto.CVId())); }
        e.setEducationalInstitution(dto.educationalInstitutionId() == null ? null : findEducationalInstitution(dto.educationalInstitutionId()));
        if (dto.cvsIds() != null) { new ArrayList<>(e.getCvs()).forEach(e::removeCv); dto.cvsIds().forEach(i -> e.addCv(findCV(i))); }
    }

    private StudentsDto toDto(Students e) {
        return new StudentsDto(e.getId(),
                e.getStudentID(),
                e.getCV() == null ? null : e.getCV().getId(),
                e.getEducationalInstitution() == null ? null : e.getEducationalInstitution().getId(),
                e.getCvs().stream().map(CV::getId).toList());
    }
}
