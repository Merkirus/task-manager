import joblib
import os
import pandas as pd
from sklearn.ensemble import RandomForestRegressor

MODEL_PATH = os.environ.get("MODEL_PATH", "model.pkl")

def featurize(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    df['created_at'] = pd.to_datetime(df['created_at'])
    df['created_dow'] = df['created_at'].dt.weekday
    df['created_hour'] = df['created_at'].dt.hour
    if 'due_date' in df.columns:
        df['due_date'] = pd.to_datetime(df['due_date'], errors='coerce')
        df['days_until_due'] = (df['due_date'] - df['created_at']).dt.total_seconds() / 86400.0
        df['days_until_due'] = df['days_until_due'].fillna(-1)
    df['assigned_to_id'] = df.get('assigned_to_id', pd.Series([-1]*len(df))).fillna(-1)
    df['created_by_id'] = df.get('created_by_id', pd.Series([-1]*len(df))).fillna(-1)
    features = df[['priority','created_dow','created_hour','days_until_due','assigned_to_id','created_by_id']].fillna(-1)
    return features

def load_model():
    if os.path.exists(MODEL_PATH):
        return joblib.load(MODEL_PATH)
    return None

def train_from_df(df: pd.DataFrame):
    X = featurize(df)
    y = df['real_minutes']
    model = RandomForestRegressor(n_estimators=100, random_state=42)
    model.fit(X, y)
    joblib.dump(model, MODEL_PATH)
    return model
PRIORITY_MAP = {"LOW": 0, "MEDIUM": 1, "HIGH": 2}
def predict_from_dict(model, payload: dict):

    if "priority" in payload and isinstance(payload["priority"], str):
        payload["priority"] = PRIORITY_MAP.get(payload["priority"].upper(), 1)

    df = pd.DataFrame([payload])
    X = featurize(df)
    pred = model.predict(X)[0]

    if hasattr(model, "estimators_"):
        preds = [est.predict(X)[0] for est in model.estimators_]
        import numpy as np
        std = float(np.std(preds))
        confidence = max(0.0, 1.0 - std / max(1.0, abs(pred)))
    else:
        confidence = 0.5

    return {
        "predicted_minutes": int(max(0, round(pred))),
        "confidence": confidence
    }
