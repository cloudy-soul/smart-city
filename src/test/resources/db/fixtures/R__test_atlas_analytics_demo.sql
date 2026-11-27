DO $$
BEGIN
    IF to_regclass('atlas_visits') IS NULL THEN RETURN; END IF;
    INSERT INTO atlas_visits(visited_at, page, synthetic, country, region, approximate_city)
    SELECT TIMESTAMPTZ '2026-11-01 09:00:00+00' + day * interval '1 day' + visit * interval '1 minute',
           (ARRAY['HOME','TOURISM','TOURISM','EDUCATION','SPORTS','JOBS','BUSINESS','CITY','SEARCH'])
               [1 + (day + visit) % 9],
           true,
           (ARRAY['Morocco','Morocco','Morocco','France','Spain'])[1 + (day + visit) % 5],
           (ARRAY['Casablanca-Settat','Rabat-Salé-Kénitra','Marrakesh-Safi','Île-de-France','Community of Madrid'])[1 + (day + visit) % 5],
           (ARRAY['Casablanca','Rabat','Marrakesh','Paris','Madrid'])[1 + (day + visit) % 5]
    FROM generate_series(0,13) day
    CROSS JOIN LATERAL generate_series(1,4 + day % 5) visit
    WHERE '${demo-enabled}'::boolean
    ON CONFLICT (visited_at,page) WHERE synthetic DO NOTHING;
END
$$;
