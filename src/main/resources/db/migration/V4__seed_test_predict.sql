INSERT INTO task_predict (
    task_id,
    estimated_minutes,
    real_minutes,
    priority,
    assigned_to_id,
    created_by_id,
    created_at,
    model_version
)
VALUES
(1, 120, 140, 'HIGH', 2, 1, NOW(), 'manual'),
(2, 240, 200, 'MEDIUM', 2, 1, NOW(), 'manual'),
(3, 60,  80,  'LOW', 3, 1, NOW(), 'manual');