package com.smartcity.service;

import com.smartcity.dto.CVDto;
import com.smartcity.entity.CV;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.CVRepository;
import com.smartcity.repository.JobApplicationRepository;
import java.time.Instant;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CVService {

    private final CVRepository repository;
    private final JobApplicationRepository applications;

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private final CurrentResident resident;
    private final Path storage;

    public CVService(CVRepository repository, JobApplicationRepository applications, CurrentResident resident,
                     @Value("${app.cv.directory:${user.home}/.smartcity/cvs}") String directory) {
        this.repository = repository;
        this.applications = applications;
        this.resident = resident;
        this.storage = Path.of(directory).toAbsolutePath().normalize();
    }

    @Transactional(readOnly = true)
    public List<CVDto> findAll() {
        return repository.findByOwnerIsNull().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CVDto findById(Long id) {
        CV cv = load(id);
        if (cv.getOwner() != null) {
            throw new NotFoundException("CV " + id + " not found");
        }
        return toDto(cv);
    }

    public CVDto create(CVDto dto) {
        CV e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public CVDto update(Long id, CVDto dto) {
        CV e = loadLegacy(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(loadLegacy(id));
    }

    public record SampleCV(Long id, String title, String filename, Instant updatedAt) {
    }

    public record Download(String filename, Resource content) {
    }

    @Transactional(readOnly = true)
    public List<SampleCV> mine() {
        return repository.findByOwnerIdOrderByIdDesc(resident.requireStudent().getId())
                .stream().map(this::sampleDto).toList();
    }

    @Transactional(rollbackFor = IOException.class)
    public SampleCV upload(String title, MultipartFile file) throws IOException {
        var user = resident.requireStudent();
        String cleanedTitle = title(title);
        StoredFile stored = store(file);
        CV cv = new CV(cleanedTitle, "", Instant.now(), "application/pdf", "local", stored.key(), stored.filename(), "pdf");
        cv.setOwner(user);
        return sampleDto(repository.save(cv));
    }

    @Transactional(rollbackFor = IOException.class)
    public SampleCV editMine(Long id, String title, MultipartFile replacement) throws IOException {
        CV cv = lockMine(id);
        cv.setTitle(title(title));
        if (replacement != null) {
            requireUnused(cv);
            StoredFile stored = store(replacement);
            removeAfterCompletion(path(cv.getStoragePath()), TransactionSynchronization.STATUS_COMMITTED);
            cv.setStoragePath(stored.key());
            cv.setFileName(stored.filename());
        }
        cv.setLastUpdate(Instant.now());
        return sampleDto(cv);
    }

    public void deleteMine(Long id) {
        CV cv = lockMine(id);
        requireUnused(cv);
        repository.delete(cv);
        removeAfterCompletion(path(cv.getStoragePath()), TransactionSynchronization.STATUS_COMMITTED);
    }

    @Transactional(readOnly = true)
    public Download download(Long id) {
        Long student = resident.requireStudent().getId();
        return storedDownload(owned(load(id), student));
    }

    @Transactional(readOnly = true)
    public Download review(Long id) {
        if (resident.require().getAdmin() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required.");
        }
        CV cv = load(id);
        if (cv.getOwner() == null || !applications.existsByCV_Id(id)) {
            throw new NotFoundException("Application CV not found.");
        }
        return storedDownload(cv);
    }

    private Download storedDownload(CV cv) {
        Resource resource = new FileSystemResource(path(cv.getStoragePath()));
        if (!resource.isReadable()) {
            throw new NotFoundException("CV file not found.");
        }
        return new Download(cv.getFileName(), resource);
    }

    private CV lockMine(Long id) {
        Long student = resident.requireStudent().getId();
        CV cv = repository.lockById(id).orElseThrow(() -> new NotFoundException("CV not found."));
        return owned(cv, student);
    }

    private CV owned(CV cv, Long student) {
        if (cv.getOwner() == null || !student.equals(cv.getOwner().getId())) {
            throw new NotFoundException("CV not found.");
        }
        return cv;
    }

    private void requireUnused(CV cv) {
        if (applications.existsByCV_Id(cv.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This PDF is attached to an application.");
        }
    }

    private String title(String raw) {
        if (raw.isBlank() || raw.length() > 255) {
            throw new IllegalArgumentException("Enter a CV title of at most 255 characters.");
        }
        return raw.strip();
    }

    private record StoredFile(String key, String filename) {
    }

    private StoredFile store(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (file.isEmpty() || file.getSize() > MAX_SIZE || filename == null || filename.length() > 255
                || !filename.matches("(?i)[a-z0-9][a-z0-9 ._-]*[.]pdf")) {
            throw new IllegalArgumentException("Provide a PDF up to 5 MB with a simple filename.");
        }
        try (InputStream in = file.getInputStream()) {
            if (!java.util.Arrays.equals(in.readNBytes(5), new byte[]{'%', 'P', 'D', 'F', '-'})) {
                throw new IllegalArgumentException("Select a PDF file.");
            }
        }
        String key = UUID.randomUUID() + ".pdf";
        Path target = path(key);
        Files.createDirectories(storage);
        Files.createFile(target);
        removeAfterCompletion(target, TransactionSynchronization.STATUS_ROLLED_BACK);
        try (InputStream in = file.getInputStream(); var out = Files.newOutputStream(target)) {
            in.transferTo(out);
        }
        return new StoredFile(key, filename);
    }

    private Path path(String key) {
        if (!key.matches("[a-f0-9-]{36}[.]pdf")) {
            throw new NotFoundException("CV file not found.");
        }
        return storage.resolve(key);
    }

    private void removeAfterCompletion(Path target, int expected) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == expected) {
                    try {
                        Files.deleteIfExists(target);
                    } catch (IOException ex) {
                        org.slf4j.LoggerFactory.getLogger(CVService.class).warn("Could not remove CV file", ex);
                    }
                }
            }
        });
    }

    private CV loadLegacy(Long id) {
        CV cv = load(id);
        if (cv.getOwner() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CV metadata is managed by the upload workflow.");
        }
        return cv;
    }

    private SampleCV sampleDto(CV cv) {
        return new SampleCV(cv.getId(), cv.getTitle(), cv.getFileName(), cv.getLastUpdate());
    }

    private CV load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("CV " + id + " not found"));
    }

    private CV toEntity(CVDto dto) {
        CV e = new CV(dto.title(), dto.description(), dto.lastUpdate(), dto.fileType(), dto.storageServer(), dto.storagePath(), dto.fileName(), dto.fileExtension());
        apply(e, dto, false);
        return e;
    }

    private void apply(CV e, CVDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setTitle(dto.title()); }
        if (includeConstructorFields) { e.setDescription(dto.description()); }
        if (includeConstructorFields) { e.setLastUpdate(dto.lastUpdate()); }
        if (includeConstructorFields) { e.setFileType(dto.fileType()); }
        if (includeConstructorFields) { e.setStorageServer(dto.storageServer()); }
        if (includeConstructorFields) { e.setStoragePath(dto.storagePath()); }
        if (includeConstructorFields) { e.setFileName(dto.fileName()); }
        if (includeConstructorFields) { e.setFileExtension(dto.fileExtension()); }
    }

    private CVDto toDto(CV e) {
        return new CVDto(e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getLastUpdate(),
                e.getFileType(),
                e.getStorageServer(),
                e.getStoragePath(),
                e.getFileName(),
                e.getFileExtension());
    }
}
