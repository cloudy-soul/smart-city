package com.smartcity.service;

import com.smartcity.dto.BusinessNewsDto;
import com.smartcity.entity.BusinessNews;
import com.smartcity.entity.Company;
import com.smartcity.exception.NotFoundException;
import com.smartcity.repository.BusinessNewsRepository;
import com.smartcity.repository.CompanyRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BusinessNewsService {

    private final BusinessNewsRepository news;
    private final CompanyRepository companies;

    public BusinessNewsService(BusinessNewsRepository news, CompanyRepository companies) {
        this.news = news;
        this.companies = companies;
    }

    @Transactional(readOnly = true)
    public List<BusinessNewsDto> list(boolean includeDrafts) {
        var records = includeDrafts ? news.findAll() : news.findByPublishedAtIsNotNullOrderByPublishedAtDesc();
        return records.stream().map(this::toDto).toList();
    }

    public BusinessNewsDto save(Long id, BusinessNewsDto dto) {
        Company company = companies.findById(dto.companyId()).orElseThrow(() -> new NotFoundException("Business not found."));
        if (id == null) {
            return toDto(news.save(new BusinessNews(company, dto.title(), dto.body(), dto.category(), dto.published())));
        }
        BusinessNews record = load(id);
        record.update(company, dto.title(), dto.body(), dto.category(), dto.published());
        return toDto(record);
    }

    public void delete(Long id) { news.delete(load(id)); }

    private BusinessNews load(Long id) {
        return news.findById(id).orElseThrow(() -> new NotFoundException("Business news item not found."));
    }

    private BusinessNewsDto toDto(BusinessNews record) {
        return new BusinessNewsDto(record.getId(), record.getCompany().getId(), record.getTitle(), record.getBody(),
                record.getCategory(), record.getPublishedAt() != null, record.getPublishedAt());
    }
}
