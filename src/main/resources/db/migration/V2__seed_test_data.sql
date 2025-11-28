-- USERS
INSERT INTO users (name, email, password, role)
VALUES
    ('Admin', 'admin@test.com', '$2a$10$dummyadmin', 'ADMIN'),
    ('Alice', 'alice@test.com', '$2a$10$dummy1', 'MEMBER'),
    ('Bob',   'bob@test.com',   '$2a$10$dummy2', 'MEMBER');

-- TASKS
INSERT INTO tasks (title, description, priority, status, due_date, progress, assigned_to_id, created_by_id)
VALUES
    ('Setup project', 'Initial setup', 'HIGH', 'COMPLETED', CURRENT_DATE - 3, 100, 2, 1),
    ('Implement API', 'CRUD endpoints', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE + 3, 60, 2, 1),
    ('Write tests', 'Integration tests', 'LOW', 'PENDING', CURRENT_DATE + 7, 0, 3, 1);

-- ATTACHMENTS
INSERT INTO task_attachments (task_id, attachment)
VALUES
    (1, 'spec.pdf'),
    (2, 'api.png');

-- TODO ITEMS
INSERT INTO to_do_item (text, completed)
VALUES
    ('Create controller', true),
    ('Create service', false),
    ('Create repository', false);

-- LINK TODO → TASK
INSERT INTO task_to_do_check_list (task_id, to_do_check_list_id)
VALUES
    (2, 1),
    (2, 2),
    (2, 3);
