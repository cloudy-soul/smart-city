-- Remove known, unchanged fixtures only. Keep rows that have since been linked
-- to a booking, application, favourite, student, or other user-entered record.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM userses WHERE user_code IN ('casablanca-demo-resident','casablanca-demo-student'))
    THEN RETURN;
    END IF;

    DELETE FROM business_news n USING companies c
    WHERE n.company_id=c.id AND c.tax='DEMO-CASA' AND c.email='studio@example.invalid'
      AND n.title='Demo open studio' AND n.body='Meet the sample studio team in Maarif.'
      AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.news_id=n.id);

    DELETE FROM jobs j USING companies c
    WHERE j.company_id=c.id AND c.tax='DEMO-CASA' AND c.email='studio@example.invalid'
      AND j.title='Demo Studio Assistant' AND j.requirements='Sample portfolio'
      AND j.description='Help coordinate client projects' AND j.employment_type='Full-time'
      AND j.industry='Design' AND j.salary=6000
      AND NOT EXISTS (SELECT 1 FROM job_applications a WHERE a.job_id=j.id)
      AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.jobs_id=j.id);

    DELETE FROM companies c
    WHERE c.tax='DEMO-CASA' AND c.email='studio@example.invalid' AND c.name='Demo Casa Studio'
      AND c.description='Sample Casablanca business' AND c.domain_name='example.invalid'
      AND c.title='Design studio' AND c.address='Maarif' AND c.city='Casablanca'
      AND c.latitude=33.585 AND c.longitude=-7.635 AND c.destination IS NULL
      AND NOT EXISTS (SELECT 1 FROM company_specialties s WHERE s.company_id=c.id)
      AND NOT EXISTS (SELECT 1 FROM jobs j WHERE j.company_id=c.id)
      AND NOT EXISTS (SELECT 1 FROM business_news n WHERE n.company_id=c.id)
      AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.business_id=c.id);

    WITH removed AS (
        DELETE FROM sport_events e USING venues v, places p, venue_slots s
        WHERE e.venue_id=v.id AND v.id=p.id AND e.slot_id=s.id
          AND p.name='Demo Maarif Sports Hall' AND p.description='Sample community venue'
          AND e.title='Demo Community Basketball' AND e.description='Sample evening match'
          AND e.ticket_price=40 AND e.ticket_capacity=50 AND e.reserved_tickets=0
          AND s.ends_at-s.starts_at=interval '2 hours'
          AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.event_id=e.id)
          AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.sports_id=e.id)
        RETURNING e.slot_id
    )
    DELETE FROM venue_slots WHERE id IN (SELECT slot_id FROM removed);

    WITH removed AS (
        DELETE FROM venues v USING places p
        WHERE v.id=p.id AND p.name='Demo Maarif Sports Hall' AND p.description='Sample community venue'
          AND v.capacity=100 AND v.venue_type='Sports hall'
          AND NOT EXISTS (SELECT 1 FROM venue_slots s WHERE s.venue_id=v.id)
          AND NOT EXISTS (SELECT 1 FROM sport_events e WHERE e.venue_id=v.id)
          AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.venue_id=v.id)
        RETURNING v.id
    )
    DELETE FROM places WHERE id IN (SELECT id FROM removed);

    WITH removed AS (
        DELETE FROM tourism_places t USING places p
        WHERE t.id=p.id AND p.name='Demo Corniche Walk' AND p.description='A sample waterfront itinerary'
          AND p.address='Ain Diab' AND p.latitude=33.594 AND p.longitude=-7.662
          AND t.category='Outdoors' AND t.opening_hours='Daily' AND t.rating=4.5
          AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.tourism_id=t.id)
        RETURNING t.id
    )
    DELETE FROM places WHERE id IN (SELECT id FROM removed);

    WITH removed AS (
        DELETE FROM educational_institutions i USING places p
        WHERE i.id=p.id AND p.name='Demo Casablanca Learning Centre' AND p.description='Sample evening courses'
          AND p.address='Maarif' AND p.latitude=33.585 AND p.longitude=-7.637
          AND i.contact_email='learning@example.invalid' AND i.website='https://example.invalid'
          AND NOT EXISTS (SELECT 1 FROM studentses s WHERE s.educational_institution_id=i.id)
          AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.education_id=i.id)
        RETURNING i.id
    )
    DELETE FROM places WHERE id IN (SELECT id FROM removed);

    DELETE FROM cities c
    WHERE c.name='Casablanca' AND c.description='Casablanca demo directory'
      AND c.latitude=33.5731 AND c.longitude=-7.5898
      AND NOT EXISTS (SELECT 1 FROM places p WHERE p.city_id=c.id);

    WITH removed AS (
        DELETE FROM userses u
        WHERE u.user_code IN ('casablanca-demo-resident','casablanca-demo-student')
          AND u.email IN ('resident@example.invalid','student@example.invalid')
          AND u.password_hash IS NULL AND u.admin_id IS NULL
          AND NOT EXISTS (SELECT 1 FROM users_roles r WHERE r.users_id=u.id)
          AND NOT EXISTS (SELECT 1 FROM cvs c WHERE c.owner_id=u.id)
          AND NOT EXISTS (SELECT 1 FROM job_applications a WHERE a.users_id=u.id)
          AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.users_id=u.id)
          AND NOT EXISTS (SELECT 1 FROM favourites f WHERE f.user_id=u.id)
        RETURNING id, students_id
    )
    DELETE FROM studentses s WHERE s.id IN (SELECT students_id FROM removed)
      AND s.studentid IN (910001,910002) AND s.cv_id IS NULL AND s.educational_institution_id IS NULL
      AND NOT EXISTS (SELECT 1 FROM userses u WHERE u.students_id=s.id AND u.id NOT IN (SELECT id FROM removed))
      AND NOT EXISTS (SELECT 1 FROM cvs c WHERE c.students_id=s.id);
END
$$;
