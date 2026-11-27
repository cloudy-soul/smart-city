package com.smartcity.controller;

import com.smartcity.dto.CVDto;
import com.smartcity.service.CVService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
@RequestMapping("/api/cvs")
public class CVController {

    private final CVService service;

    public CVController(CVService service) {
        this.service = service;
    }

    @GetMapping
    public List<CVDto> list() {
        return service.findAll();
    }

    @GetMapping("/mine")
    public List<CVService.SampleCV> mine() { return service.mine(); }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CVService.SampleCV upload(@RequestParam String title, @RequestParam MultipartFile file) throws IOException {
        return service.upload(title, file);
    }

    @PutMapping(value = "/mine/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CVService.SampleCV editMine(@PathVariable Long id, @RequestParam String title,
                                      @RequestParam(required = false) MultipartFile file) throws IOException {
        return service.editMine(id, title, file != null && !file.isEmpty() ? file : null);
    }

    @DeleteMapping("/mine/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMine(@PathVariable Long id) {
        service.deleteMine(id);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> file(@PathVariable Long id) {
        return attachment(service.download(id));
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<Resource> review(@PathVariable Long id) {
        return attachment(service.review(id));
    }

    private ResponseEntity<Resource> attachment(CVService.Download file) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file.filename()).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(file.content());
    }

    @GetMapping("/{id}")
    public CVDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CVDto create(@Valid @RequestBody CVDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CVDto update(@PathVariable Long id, @Valid @RequestBody CVDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
