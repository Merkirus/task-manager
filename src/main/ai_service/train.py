import os
import pandas as pd
import joblib
from model import train_from_df
from dotenv import load_dotenv
import psycopg2
from psycopg2.extras import RealDictCursor

load_dotenv()

DB_CONFIG = {
    "dbname": os.getenv("DB_NAME", "task_manager"),
    "user": os.getenv("DB_USER", "postgres"),
    "password": os.getenv("DB_PASS", "1234"),
    "host": os.getenv("DB_HOST", "localhost"),
    "port": int(os.getenv("DB_PORT", 5432))
}

MODEL_PATH = os.getenv("MODEL_PATH", "model.pkl")
def load_training_data_from_db():
    import psycopg2
    import pandas as pd

    conn = psycopg2.connect(
        host="localhost",
        dbname="task_manager",
        user="postgres",
        password="1234"
    )
    cur = conn.cursor()

    cur.execute("""
                SELECT
                    priority,
                    created_at::text,
                    due_date::text,
                    assigned_to_id,
                    created_by_id,
                    EXTRACT(EPOCH FROM (due_date - created_at)) / 60 AS real_minutes
                FROM task
                WHERE due_date IS NOT NULL
                """)

    rows = cur.fetchall()
    columns = ["priority", "created_at", "due_date", "assigned_to_id", "created_by_id", "real_minutes"]
    df = pd.DataFrame(rows, columns=columns)
    priority_map = {"LOW": 0, "MEDIUM": 1, "HIGH": 2}
    df['priority'] = df['priority'].map(priority_map)


    df['assigned_to_id'] = df.get('assigned_to_id', pd.Series([-1]*len(df))).fillna(-1)
    df['created_by_id'] = df.get('created_by_id', pd.Series([-1]*len(df))).fillna(-1)

    cur.close()
    conn.close()

    return df

def main():
    df = load_training_data_from_db()
    if df is None:
        print("No data to train on")
        return
    model = train_from_df(df)
    print("Trained model saved")

if __name__ == "__main__":
    main()
