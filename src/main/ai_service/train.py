from model import train_from_df
import psycopg2
import pandas as pd
import os
from datetime import datetime

DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "dbname": "task_manager_clean",
    "user": "postgres",
    "password": "1234",
    "options": "-c search_path=public"
}

def load_training_data():
    conn = psycopg2.connect(**DB_CONFIG)
    query = """
            SELECT
                t.priority,
                t.created_at,
                t.due_date,
                t.assigned_to_id,
                t.created_by_id,
                tp.real_minutes
            FROM tasks t
                     JOIN task_predict tp ON tp.task_id = t.id
            WHERE
                t.status = 'COMPLETED'
              AND tp.real_minutes IS NOT NULL

            """

    df = pd.read_sql(query, conn)
    conn.close()

    return df

def train_model_from_db():
    df = load_training_data()
    model = train_from_df(df)
    return model
