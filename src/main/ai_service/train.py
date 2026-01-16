from model import train_from_df
import psycopg2
import pandas as pd
import os

def load_training_data():
    conn = psycopg2.connect(
        host="localhost",
        port=5432,
        dbname="task_manager",
        user="postgres",
        password="1234",
        options="-c search_path=public"
    )



def train_model_from_db():
    df = load_training_data


    print("ROWS:", len(df))
    print(df.head())

    if df.empty:
        raise ValueError("Brak danych treningowych")

    model = train_from_df(df)
    return model
