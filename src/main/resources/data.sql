
INSERT INTO family (name) VALUES ('Smith Family');
INSERT INTO Member (first_name, last_name, dob, family_id, is_adopted) VALUES
                                                                                 ('John', 'Smith', '1940-01-01', 1, FALSE),  -- Grandfather
                                                                                 ('Mary', 'Smith', '1945-01-01', 1, FALSE), -- Grandmother
                                                                                 ('James', 'Smith', '1965-01-01', 1, FALSE),  -- Son 1
                                                                                 ('Robert', 'Smith', '1970-01-01', 1, FALSE), -- Son 2
                                                                                 ('Linda', 'Smith', '1967-01-01', 1, FALSE), -- James' Wife
                                                                                 ('Anna', 'Smith', '1972-01-01', 1, FALSE), -- Robert's Wife
                                                                                 ('Emily', 'Smith', '1990-01-01', 1, FALSE), -- James' Daughter
                                                                                 ('Michael', 'Smith', '1993-01-01', 1, FALSE), -- James' Son
                                                                                 ('Laura', 'Smith', '2000-01-01', 1, FALSE); -- Robert's Daughter

INSERT INTO marriage (Member1_id, Member2_id, wedding_date, divorce_date) VALUES
                                                                              ((SELECT id FROM Member WHERE first_name = 'John' AND last_name = 'Smith'), (SELECT id FROM Member WHERE first_name = 'Mary' AND last_name = 'Smith'), '1964-06-15', NULL),
                                                                              ((SELECT id FROM Member WHERE first_name = 'James' AND last_name = 'Smith'), (SELECT id FROM Member WHERE first_name = 'Linda' AND last_name = 'Smith'), '1989-05-20', NULL),
                                                                              ((SELECT id FROM Member WHERE first_name = 'Robert' AND last_name = 'Smith'), (SELECT id FROM Member WHERE first_name = 'Anna' AND last_name = 'Smith'), '1998-08-25', NULL);

UPDATE Member
SET parent_id = (SELECT marriage_id FROM marriage m
    JOIN Member p ON (m.member1_id = p.id OR m.member2_id = p.id)
                           WHERE p.first_name = 'John' AND p.last_name = 'Smith')
WHERE first_name = 'James' AND last_name = 'Smith';

UPDATE Member
SET parent_id = (SELECT marriage_id FROM marriage m
    JOIN Member p ON (m.member1_id = p.id OR m.member2_id = p.id)
                           WHERE p.first_name = 'James' AND p.last_name = 'Smith')
WHERE first_name = 'Emily' AND last_name = 'Smith';