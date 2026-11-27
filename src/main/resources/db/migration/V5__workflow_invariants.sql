ALTER TABLE venue_slots
    ADD CONSTRAINT venue_slots_finite_dates CHECK (isfinite(starts_at) AND isfinite(ends_at)),
    ADD CONSTRAINT venue_slots_event_key UNIQUE (id, venue_id, starts_at);

ALTER TABLE sport_events
    ADD CONSTRAINT event_slot_schedule_fk FOREIGN KEY (slot_id, venue_id, event_date)
        REFERENCES venue_slots(id, venue_id, starts_at),
    DROP CONSTRAINT event_slot_venue_fk,
    ADD CONSTRAINT event_capacity_requires_slot CHECK (ticket_capacity = 0 OR slot_id IS NOT NULL),
    ADD CONSTRAINT event_finite_date CHECK (isfinite(event_date)) NOT VALID;

ALTER TABLE venue_slots DROP CONSTRAINT venue_slots_id_venue_unique;
ALTER TABLE jobs ADD CONSTRAINT job_finite_deadline CHECK (isfinite(application_deadline));
ALTER TABLE places ADD CONSTRAINT place_coordinate_bounds
    CHECK (latitude BETWEEN -90 AND 90 AND longitude BETWEEN -180 AND 180) NOT VALID;
CREATE INDEX bookings_event_fk_idx ON bookings(event_id);
CREATE INDEX business_news_company_fk_idx ON business_news(company_id);
