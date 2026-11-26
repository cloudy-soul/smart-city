package com.smartcity.service;

import com.smartcity.dto.TouristsDto;
import com.smartcity.entity.Tourists;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.TouristsRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TouristsService {

    private final TouristsRepository repository;

    public TouristsService(TouristsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TouristsDto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TouristsDto findById(Long id) {
        return toDto(load(id));
    }

    public TouristsDto create(TouristsDto dto) {
        Tourists e = toEntity(dto);
        return toDto(repository.save(e));
    }

    public TouristsDto update(Long id, TouristsDto dto) {
        Tourists e = load(id);
        apply(e, dto, true);
        return toDto(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(load(id));
    }

    private Tourists load(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Tourists " + id + " not found"));
    }

    private Tourists toEntity(TouristsDto dto) {
        Tourists e = new Tourists(dto.touristID(), dto.preferredLanguage(), dto.travelPurpose());
        apply(e, dto, false);
        return e;
    }

    private void apply(Tourists e, TouristsDto dto, boolean includeConstructorFields) {
        if (includeConstructorFields) { e.setTouristID(dto.touristID()); }
        if (includeConstructorFields) { e.setPreferredLanguage(dto.preferredLanguage()); }
        if (includeConstructorFields) { e.setTravelPurpose(dto.travelPurpose()); }
    }

    private TouristsDto toDto(Tourists e) {
        return new TouristsDto(e.getId(),
                e.getTouristID(),
                e.getPreferredLanguage(),
                e.getTravelPurpose());
    }
}
