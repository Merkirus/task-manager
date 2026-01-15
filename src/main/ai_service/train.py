from model import train_from_df
import psycopg2
import pandas as pd
import os

def load_training_data():
    conn = psycopg2.connect(
        host="localhost",
        dbname="task_manager",
        user="postgres",
        password="1234"
    )

    query = """
            SELECT
                t.priority,
                t.created_at,
                t.due_date,
                t.assigned_to_id,
                t.created_by_id,
                tp.real_minutes
            FROM task t
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
