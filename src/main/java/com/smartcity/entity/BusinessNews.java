package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "business_news")
public class BusinessNews extends BaseEntity {

    public enum Category { NEWS, ANNOUNCEMENT }

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private Instant publishedAt;

    protected BusinessNews() {
    }

    public BusinessNews(Company company, String title, String body, Category category, boolean published) {
        update(company, title, body, category, published);
    }

    public void update(Company company, String title, String body, Category category, boolean published) {
        this.company = company;
        this.title = title;
        this.body = body;
        this.category = category;
        if (!published) {
            publishedAt = null;
        } else if (publishedAt == null) {
            publishedAt = Instant.now();
        }
    }

    public Company getCompany() { return company; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public Category getCategory() { return category; }
    public Instant getPublishedAt() { return publishedAt; }
}
