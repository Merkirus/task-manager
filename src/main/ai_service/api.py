from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from model import load_model, predict_from_dict
from train import train_model_from_db
from dotenv import load_dotenv
import os

load_dotenv()

app = FastAPI(title="Task Prediction Service")
MODEL = None


class PredictRequest(BaseModel):
    priority: str
    created_at: str
    due_date: str | None = None
    assigned_to_id: int | None = None
    created_by_id: int | None = None

class PredictResponse(BaseModel):
    predicted_minutes: int
    confidence: float
    model_version: str | None = None


@app.on_event("startup")
def startup():
    global MODEL
    MODEL = load_model()
    if MODEL:
        print("Model loaded")
    else:
        print("Model not trained")


@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    if MODEL is None:
        raise HTTPException(503, "Model not trained")

    result = predict_from_dict(MODEL, req.dict())

    return PredictResponse(
        predicted_minutes=result["predicted_minutes"],
        confidence=result["confidence"],
        model_version=os.getenv("MODEL_VERSION", "v1")
    )


@app.post("/train")
def train():
    global MODEL
    try:
        MODEL = train_model_from_db()
        return {
            "status": "trained",
            "model_version": os.getenv("MODEL_VERSION", "v1")
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
