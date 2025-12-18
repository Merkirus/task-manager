import joblib
import os
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor

MODEL_PATH = os.environ.get("MODEL_PATH", "model.pkl")

PRIORITY_MAP = {
    "LOW": 0,
    "MEDIUM": 1,
    "HIGH": 2
}


def featurize(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()

    # priority: string -> int (SAFE)
    if df["priority"].dtype == object:
        df["priority"] = df["priority"].str.upper().map(PRIORITY_MAP)

    df["priority"] = df["priority"].fillna(1)  #med

    #dates
    df["created_at"] = pd.to_datetime(df["created_at"], errors="coerce")
    df["created_dow"] = df["created_at"].dt.weekday.fillna(0)
    df["created_hour"] = df["created_at"].dt.hour.fillna(12)

    if "due_date" in df.columns:
        df["due_date"] = pd.to_datetime(df["due_date"], errors="coerce")
        df["days_until_due"] = (
            (df["due_date"] - df["created_at"])
            .dt.total_seconds()
            .div(86400)
        ).fillna(-1)
    else:
        df["days_until_due"] = -1


    df["assigned_to_id"] = df.get("assigned_to_id", -1).fillna(-1)
    df["created_by_id"] = df.get("created_by_id", -1).fillna(-1)

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
    X = featurize(df)
    y = df["real_minutes"]

    model = RandomForestRegressor(
        n_estimators=200,
        random_state=42,
        n_jobs=-1
    )

    model.fit(X, y)
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
