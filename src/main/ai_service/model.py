import joblib
import os
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
import joblib

MODEL_PATH = os.environ.get("MODEL_PATH", "model.pkl")

PRIORITY_MAP = {
    "LOW": 0,
    "MEDIUM": 1,
    "HIGH": 2
}


def featurize(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()

    if df["priority"].dtype == object:
        df["priority"] = (
            df["priority"]
            .astype(str)
            .str.strip()
            .str.upper()
            .map(PRIORITY_MAP)
        )
    if df["priority"].dtype == str:
        df["priority"] = (
            df["priority"]
            .astype(str)
            .str.strip()
            .str.upper()
            .map(PRIORITY_MAP)
        )
    df["priority"] = df["priority"].fillna(1)  #med
    df["priority"] = (
        df["priority"]
        .astype(str)
        .str.strip()
        .str.upper()
        .map(PRIORITY_MAP)
        .fillna(1)
        .astype(int)
    )

    #dates
    df["created_at"] = pd.to_datetime(df["created_at"], errors="coerce").fillna(pd.Timestamp("2000-01-01"))
    df["created_dow"] = df["created_at"].dt.weekday
    df["created_hour"] = df["created_at"].dt.hour

    if "due_date" in df.columns:
        df["due_date"] = pd.to_datetime(df["due_date"], errors="coerce")
        df["days_until_due"] = (
            (df["due_date"] - df["created_at"])
            .dt.total_seconds()
            .div(86400)
        ).fillna(-1)
    else:
        df["days_until_due"] = -1


    df["assigned_to_id"] =  pd.to_numeric(df.get("assigned_to_id", -1), errors="coerce").fillna(-1).astype(int)
    df["created_by_id"] = pd.to_numeric(df.get("created_by_id", -1), errors="coerce").fillna(-1).astype(int)

    return df[
        [
            "priority",
            "created_dow",
            "created_hour",
            "days_until_due",
            "assigned_to_id",
            "created_by_id",
        ]
    ]



def load_model():
    if os.path.exists(MODEL_PATH):
        return joblib.load(MODEL_PATH)
    return None



def train_from_df(df: pd.DataFrame):

    df = df.copy()
    df["real_minutes"] = pd.to_numeric(df["real_minutes"], errors="coerce")
    df = df.dropna(subset=["real_minutes"])

    if df.empty:
        raise ValueError("Brak danych po czyszczeniu real_minutes")

    X = featurize(df)
    y = df["real_minutes"]

    #print("X HEAD ")
    #print(X.head())
    #print("\nX DTYPES")
    #print(X.dtypes)


    X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )
    #print(f"Samples total : {len(df)}")
    #print(f"Train samples: {len(X_train)}")
    #print(f"Test samples : {len(X_test)}")

    model = RandomForestRegressor(
        n_estimators=200,
        random_state=42,
        n_jobs=-1
    )

    model.fit(X_train, y_train)

    joblib.dump(model, MODEL_PATH)
    return model



def predict_from_dict(model, payload: dict):
    df = pd.DataFrame([payload])
    X = featurize(df)

    pred = model.predict(X)[0]

    if hasattr(model, "estimators_"):
        preds = np.array([est.predict(X)[0] for est in model.estimators_])
        std = preds.std()
        confidence = max(0.0, 1.0 - std / max(1.0, abs(pred)))
    else:
        confidence = 0.5

    return {
        "predicted_minutes": int(max(0, round(pred))),
        "confidence": float(round(confidence, 3)),
    }
