DO $$
DECLARE
    resident bigint;
    student bigint;
BEGIN
    IF NOT '${demo-enabled}'::boolean THEN RETURN; END IF;
    SELECT id INTO resident FROM userses WHERE user_code = 'casablanca-demo-resident' ORDER BY id LIMIT 1;
    IF resident IS NOT NULL AND (SELECT students_id FROM userses WHERE id = resident) IS NULL THEN
        INSERT INTO studentses(studentid) VALUES (910001) RETURNING id INTO student;
        UPDATE userses SET students_id = student WHERE id = resident;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM userses WHERE user_code = 'casablanca-demo-student') THEN
        INSERT INTO studentses(studentid) VALUES (910002) RETURNING id INTO student;
        INSERT INTO userses(firstname,lastname,email,phone,country,city,region,address,nationality,birthday,zipcode,nationalid,user_code,students_id)
        VALUES ('Demo','Student','student@example.invalid','000','Morocco','Casablanca','Casablanca-Settat',
                'Sample address','Moroccan','2000-01-01','20000','DEMO-STUDENT','casablanca-demo-student',student);
    END IF;
END
$$;
