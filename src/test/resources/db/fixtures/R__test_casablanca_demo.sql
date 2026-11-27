DO $$
DECLARE
    casablanca bigint;
    place_id bigint;
    venue bigint;
    company bigint;
    slot bigint;
    start_time timestamp := (current_date + 45) + time '18:00';
BEGIN
    IF NOT '${demo-enabled}'::boolean OR EXISTS (
        SELECT 1 FROM userses WHERE user_code = 'casablanca-demo-resident'
    ) THEN
        RETURN;
    END IF;
    SELECT id INTO casablanca FROM cities WHERE name = 'Casablanca' AND country = 'Morocco' ORDER BY id LIMIT 1;
    IF casablanca IS NULL THEN
        INSERT INTO cities(name,country,description,latitude,longitude)
        VALUES ('Casablanca','Morocco','Casablanca demo directory',33.5731,-7.5898) RETURNING id INTO casablanca;
    END IF;

    INSERT INTO places(name,description,address,city_id,latitude,longitude)
    VALUES ('Demo Corniche Walk','A sample waterfront itinerary','Ain Diab',casablanca,33.594,-7.662) RETURNING id INTO place_id;
    INSERT INTO tourism_places(id,category,opening_hours,rating) VALUES (place_id,'Outdoors','Daily',4.5);

    INSERT INTO places(name,description,address,city_id,latitude,longitude)
    VALUES ('Demo Casablanca Learning Centre','Sample evening courses','Maarif',casablanca,33.585,-7.637) RETURNING id INTO place_id;
    INSERT INTO educational_institutions(id,institution_type,contact_email,website)
    VALUES (place_id,'Training centre','learning@example.invalid','https://example.invalid');

    INSERT INTO places(name,description,address,city_id,latitude,longitude)
    VALUES ('Demo Maarif Sports Hall','Sample community venue','Maarif',casablanca,33.581,-7.632) RETURNING id INTO venue;
    INSERT INTO venues(id,venue_type,capacity) VALUES (venue,'Sports hall',100);
    INSERT INTO venue_slots(venue_id,starts_at,ends_at)
    VALUES (venue,start_time,start_time + interval '2 hours') RETURNING id INTO slot;
    INSERT INTO sport_events(title,description,event_date,ticket_price,venue_id,slot_id,ticket_capacity)
    VALUES ('Demo Community Basketball','Sample evening match',start_time,40,venue,slot,50);

    INSERT INTO companies(name,title,description,country,city,region,address,phone,email,tax,domain_name,latitude,longitude)
    VALUES ('Demo Casa Studio','Design studio','Sample Casablanca business','Morocco','Casablanca','Casablanca-Settat',
            'Maarif','+212 000000000','studio@example.invalid','DEMO-CASA','example.invalid',33.585,-7.635) RETURNING id INTO company;
    INSERT INTO jobs(title,description,company_id,employment_type,industry,salary,application_deadline,requirements)
    VALUES ('Demo Studio Assistant','Help coordinate client projects',company,'Full-time','Design',6000,current_date + 30,'Sample portfolio');
    INSERT INTO business_news(company_id,title,body,published_at)
    VALUES (company,'Demo open studio','Meet the sample studio team in Maarif.',now());

    INSERT INTO userses(firstname,lastname,email,phone,country,city,region,address,nationality,birthday,zipcode,nationalid,user_code)
    VALUES ('Demo','Resident','resident@example.invalid','000','Morocco','Casablanca','Casablanca-Settat','Sample address',
            'Moroccan','1995-01-01','20000','DEMO','casablanca-demo-resident');
END
$$;
