CREATE TABLE atlas_visits (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    visited_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP CHECK (isfinite(visited_at)),
    page varchar(24) NOT NULL CHECK (page IN ('HOME','CITY','SEARCH','TOURISM','EDUCATION','SPORTS','JOBS','BUSINESS','PRIVACY','TERMS')),
    synthetic boolean NOT NULL,
    country varchar(80),
    region varchar(80),
    approximate_city varchar(80),
    CHECK (region IS NULL OR country IS NOT NULL),
    CHECK (approximate_city IS NULL OR region IS NOT NULL),
    CHECK (synthetic OR (country IS NULL AND region IS NULL AND approximate_city IS NULL))
);

CREATE INDEX atlas_visits_source_date_idx ON atlas_visits(synthetic, visited_at);
CREATE UNIQUE INDEX atlas_visits_demo_unique ON atlas_visits(visited_at,page) WHERE synthetic;
