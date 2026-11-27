package com.smartcity.repository;

import com.smartcity.entity.BusinessNews;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessNewsRepository extends JpaRepository<BusinessNews, Long> {
    List<BusinessNews> findByPublishedAtIsNotNullOrderByPublishedAtDesc();
}
