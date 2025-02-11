insert into users (first_name, last_name, username, password, is_active) values
('John', 'Doe', 'John.Doe', 'RM9AVLZpCK', true),
('Alice', 'Smith', 'Alice.Smith', 'yh5TUyvHXz', true),
('Bob', 'Brown', 'Bob.Brown', 'CTPEeje8Jq', true),
('Emma', 'Wilson', 'Emma.Wilson', 'hZ29UTGBHk', true),
('James', 'Taylor', 'James.Taylor', 'F5ZUtZmEA5', true),
('Sophia', 'White', 'Sophia.White', 'BdescQ9GTr', true),
('Daniel', 'Martinez', 'Daniel.Martinez', 'pvr4FdufUz', true),
('Olivia', 'Clark', 'Olivia.Clark', 'R4K6DxtFBf', true),
('Ethan', 'Anderson', 'Ethan.Anderson', 'Tkq43QuyBh', true),
('Liam', 'Roberts', 'Liam.Roberts', 'ssREjuJqLv', true);

insert into trainees (date_of_birth, address, user_id) values
('1990-05-15', '123 Main St, City A', 1),  -- John Doe
('1985-09-20', '456 Oak St, City B', 2),  -- Alice Smith
('1998-12-10', '789 Pine St, City C', 3),  -- Bob Brown
('1995-03-22', '321 Birch St, City D', 7), -- Daniel Martinez
('2000-07-30', '654 Maple St, City E', 8); -- Olivia Clark

insert into training_types (name) values
('Strength Training'),
('Cardio Workouts'),
('Yoga & Meditation'),
('CrossFit'),
('Personal Training');

insert into trainers (specialization, user_id) values
(1, 4),  -- Emma Wilson -> Strength Training
(2, 5),  -- James Taylor -> Cardio Workouts
(3, 6),  -- Sophia White -> Yoga & Meditation
(4, 9),  -- Ethan Anderson -> CrossFit
(5, 10); -- Liam Roberts -> Personal Training

insert into trainings (trainee_id, trainer_id, name, type_id, date, duration) values
(1, 4, 'Full Body Strength', 1, '2024-02-10', 60),  -- John trains with Emma
(2, 5, 'HIIT Cardio', 2, '2024-02-11', 45),        -- Alice trains with James
(3, 6, 'Morning Yoga', 3, '2024-02-12', 75),       -- Bob trains with Sophia
(1, 5, 'Interval Running', 2, '2024-02-13', 30),   -- John with James
(2, 6, 'Power Yoga', 3, '2024-02-14', 90),        -- Alice with Sophia
(7, 9, 'CrossFit Challenge', 4, '2024-02-15', 60),-- Daniel with Ethan
(8, 10, 'Personalized Strength', 5, '2024-02-16', 50), -- Olivia with Liam
(3, 4, 'Upper Body Strength', 1, '2024-02-17', 45), -- Bob with Emma
(7, 10, 'One-on-One Coaching', 5, '2024-02-18', 60), -- Daniel with Liam
(8, 9, 'High-Intensity CrossFit', 4, '2024-02-19', 70); -- Olivia with Ethan
