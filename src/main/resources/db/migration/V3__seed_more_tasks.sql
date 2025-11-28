-- dodatkowi użytkownicy (opcjonalnie)
INSERT INTO users (name, email, password, role)
VALUES
    ('Charlie', 'charlie@test.com', '$2a$10$dummy3', 'MEMBER'),
    ('Diana',   'diana@test.com',   '$2a$10$dummy4', 'MEMBER')
    ON CONFLICT (email) DO NOTHING;

-- helper: id userów (zakładamy, że seed V2 już poszedł)
-- Admin: 1, Alice: 2, Bob: 3, Charlie: 4, Diana: 5 (jeśli V2 wchodzi jako pierwsze)
-- Jeśli u Ciebie IDs mogą się różnić: to i tak zadziała, jeśli masz czystą bazę na test.

-- więcej tasków (mix status/priority/progress i dat)
INSERT INTO tasks (title, description, priority, status, due_date, progress, assigned_to_id, created_by_id)
VALUES
    ('Refactor security', 'JWT filter cleanup', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + 2, 40, 2, 1),
    ('Fix N+1 queries', 'Optimize JPA fetch', 'MEDIUM', 'PENDING', CURRENT_DATE + 10, 0, 3, 1),
    ('Prepare release notes', 'v1.0 notes', 'LOW', 'COMPLETED', CURRENT_DATE - 1, 100, 5, 1),

    -- overdue cases
    ('Pay tech debt', 'cleanup warnings', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - 4, 70, 2, 1),
    ('Bugfix: attachments', 'ElementCollection mapping', 'HIGH', 'PENDING', CURRENT_DATE - 2, 0, 3, 1),

    -- long horizon
    ('Add dashboard charts', 'status + priority distribution', 'MEDIUM', 'PENDING', CURRENT_DATE + 21, 0, 4, 1),
    ('Docs: API usage', 'Swagger + auth', 'LOW', 'IN_PROGRESS', CURRENT_DATE + 14, 30, 5, 1),

    -- completed variety
    ('Implement /tasks/me', 'Dashboard endpoint', 'HIGH', 'COMPLETED', CURRENT_DATE - 7, 100, 2, 1),
    ('Checklist endpoints', 'add/update/toggle/delete', 'MEDIUM', 'COMPLETED', CURRENT_DATE - 6, 100, 3, 1),

    -- edge progress
    ('Edge: progress 0', 'newly created task', 'LOW', 'PENDING', CURRENT_DATE + 1, 0, 4, 1),
    ('Edge: progress 99', 'almost done', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + 1, 99, 5, 1);

-- attachments do wybranych nowych tasków (zakładam, że te nowe taski dostaną ID po ostatnim z V2)
-- Bezpieczniej: używamy selektów po title (unikalne w seedzie)
INSERT INTO task_attachments (task_id, attachment)
SELECT t.id, a.attachment
FROM tasks t
         JOIN (VALUES
                   ('Refactor security', 'jwt_refactor.md'),
                   ('Fix N+1 queries', 'profiling.txt'),
                   ('Add dashboard charts', 'charts_mock.png'),
                   ('Docs: API usage', 'swagger_guide.pdf')
) AS a(title, attachment)
              ON t.title = a.title;

-- TODO items (tworzymy kilka)
INSERT INTO to_do_item (text, completed) VALUES
                                             ('Write unit tests', false),
                                             ('Write integration tests', false),
                                             ('Review PR', false),
                                             ('Update README', true),
                                             ('Verify migrations', true),
                                             ('Add indexes', false);

-- link checklist do tasków (join table)
-- przypinamy po 2-3 itemy do paru tasków
INSERT INTO task_to_do_check_list (task_id, to_do_check_list_id)
SELECT t.id, i.id
FROM tasks t
         JOIN to_do_item i ON i.text IN ('Write unit tests','Review PR')
WHERE t.title IN ('Refactor security','Fix N+1 queries');

INSERT INTO task_to_do_check_list (task_id, to_do_check_list_id)
SELECT t.id, i.id
FROM tasks t
         JOIN to_do_item i ON i.text IN ('Verify migrations','Add indexes','Update README')
WHERE t.title IN ('Add dashboard charts','Docs: API usage');
