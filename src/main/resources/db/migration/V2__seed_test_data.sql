-- V2__seed.sql (deterministic)
-- PostgreSQL seed: ~500 rows per table, stable across runs, no random()

BEGIN;

------------------------------------------------------------
-- USERS (500)
------------------------------------------------------------
INSERT INTO users (name, email, password, role, created_at, updated_at)
SELECT
    'User ' || gs AS name,
    'user' || gs || '@example.com' AS email,
    md5('password-' || gs) AS password,
    CASE WHEN gs <= 10 THEN 'ADMIN' ELSE 'MEMBER' END AS role,
    now() - ((gs % 365) || ' days')::interval AS created_at,
    now() - ((gs % 30)  || ' days')::interval AS updated_at
FROM generate_series(1, 500) gs;

------------------------------------------------------------
-- TASKS (500)
-- status cycles evenly: PENDING / IN_PROGRESS / COMPLETED
-- progress consistent with status:
--   PENDING      -> 0..19
--   IN_PROGRESS  -> 20..99
--   COMPLETED    -> 100
------------------------------------------------------------
INSERT INTO tasks (
    title, description, priority, status, due_date, progress,
    created_at, updated_at,
    assigned_to_id, created_by_id
)
SELECT
    'Task #' || gs AS title,
    'Seeded task description for task #' || gs AS description,

    (ARRAY['LOW','MEDIUM','HIGH'])[1 + (gs % 3)] AS priority,
  (ARRAY['PENDING','IN_PROGRESS','COMPLETED'])[1 + (gs % 3)] AS status,

  (current_date + ((gs % 120) - 60))::date AS due_date,

  CASE (gs % 3)
    WHEN 0 THEN gs % 20                -- PENDING: 0..19
    WHEN 1 THEN 20 + (gs % 80)         -- IN_PROGRESS: 20..99
    ELSE 100                           -- COMPLETED
END AS progress,

  now() - ((gs % 180) || ' days')::interval AS created_at,
  now() - ((gs % 90)  || ' days')::interval AS updated_at,

  1 + ((gs * 37) % 500) AS assigned_to_id,
  1 + ((gs * 53) % 500) AS created_by_id
FROM generate_series(1, 500) gs;

------------------------------------------------------------
-- ATTACHMENTS (500) (1 per task)
------------------------------------------------------------
INSERT INTO task_attachments (task_id, attachment)
SELECT
    t.id,
    's3://bucket/tasks/' || t.id || '/attachment_' || t.id || '.txt'
FROM (
         SELECT id, row_number() OVER (ORDER BY id) rn
         FROM tasks
     ) t
WHERE t.rn <= 500;

------------------------------------------------------------
-- TODO ITEMS (500)
-- ~35% completed (gs % 100 < 35)
------------------------------------------------------------
INSERT INTO to_do_item (text, completed)
SELECT
    'Todo item #' || gs AS text,
    (gs % 100) < 35 AS completed
FROM generate_series(1, 500) gs;

------------------------------------------------------------
-- TASK ↔ TODO ITEMS JOIN TABLE (500 rows)
-- 1:1 mapping: task rn -> todo rn
------------------------------------------------------------
INSERT INTO task_to_do_check_list (task_id, to_do_check_list_id)
SELECT
    t.id AS task_id,
    i.id AS to_do_check_list_id
FROM (
         SELECT id, row_number() OVER (ORDER BY id) rn
         FROM tasks
     ) t
         JOIN (
    SELECT id, row_number() OVER (ORDER BY id) rn
    FROM to_do_item
) i
              ON i.rn = t.rn
WHERE t.rn <= 500;

------------------------------------------------------------
-- TASK_PREDICT (500) (1 per task)
-- priority matches tasks priority (derived from rn)
-- estimated_minutes: 15..480 (pattern)
-- real_minutes: sometimes NULL (~15%), otherwise 10..600 (pattern)
-- assigned_to_id / created_by_id consistent with tasks ids pattern
------------------------------------------------------------
INSERT INTO task_predict (
    task_id, assigned_to_id, created_by_id,
    priority, estimated_minutes, real_minutes, model_version,
    created_at
)
SELECT
    t.id AS task_id,

    -- keep consistent pattern with tasks (based on rn)
    1 + ((t.rn * 37) % 500) AS assigned_to_id,
    1 + ((t.rn * 53) % 500) AS created_by_id,

    -- consistent with tasks priority cycle
    (ARRAY['LOW','MEDIUM','HIGH'])[1 + (t.rn % 3)] AS priority,

    -- 15..480
    15 + ((t.rn * 11) % 466) AS estimated_minutes,

    -- ~15% NULL: rn % 20 in {0,1,2}
    CASE
    WHEN (t.rn % 20) IN (0,1,2) THEN NULL
    ELSE 10 + ((t.rn * 17) % 591)
END AS real_minutes,

  -- model versions cycle
  'v' || (1 + (t.rn % 3)) || '.' || (t.rn % 10) AS model_version,

  now() - ((t.rn % 90) || ' days')::interval AS created_at
FROM (
  SELECT id, row_number() OVER (ORDER BY id) rn
  FROM tasks
) t
WHERE t.rn <= 500;

COMMIT;
