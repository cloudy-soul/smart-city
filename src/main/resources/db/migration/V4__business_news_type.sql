ALTER TABLE business_news
    ADD COLUMN category varchar(255) NOT NULL DEFAULT 'NEWS'
        CHECK (category IN ('NEWS', 'ANNOUNCEMENT'));
