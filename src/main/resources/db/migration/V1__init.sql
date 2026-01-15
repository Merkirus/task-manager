-- USERS
CREATE TABLE users (
   id BIGSERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   email VARCHAR(255) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
   created_at TIMESTAMP DEFAULT now(),
   updated_at TIMESTAMP DEFAULT now(),

   CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'MEMBER'))
);

-- TASKS
CREATE TABLE tasks (
   id BIGSERIAL PRIMARY KEY,
   title VARCHAR(255) NOT NULL,
   description TEXT,
   priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
   status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
   due_date DATE NOT NULL,
   progress INT DEFAULT 0,

   assigned_to_id BIGINT,
   created_by_id BIGINT,

   CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to_id) REFERENCES users(id),
   CONSTRAINT fk_tasks_created_by  FOREIGN KEY (created_by_id)  REFERENCES users(id),

   CONSTRAINT ck_tasks_priority CHECK (priority IN ('LOW','MEDIUM','HIGH')),
   CONSTRAINT ck_tasks_status CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
   CONSTRAINT ck_tasks_progress CHECK (progress BETWEEN 0 AND 100)
);

-- ATTACHMENTS (ElementCollection<String>)
CREATE TABLE task_attachments (
    task_id BIGINT NOT NULL,
    attachment TEXT NOT NULL,

    CONSTRAINT fk_attachments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- TODO ITEMS
CREATE TABLE to_do_item (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    completed BOOLEAN DEFAULT FALSE
);

-- TASK ↔ TODO ITEMS (OneToMany bez mappedBy → join table!)
CREATE TABLE task_to_do_check_list (
    task_id BIGINT NOT NULL,
    to_do_check_list_id BIGINT NOT NULL,

    CONSTRAINT fk_task_todo_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_todo_item FOREIGN KEY (to_do_check_list_id) REFERENCES to_do_item(id) ON DELETE CASCADE
);
