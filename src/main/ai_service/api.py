import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from model import load_model, predict_from_dict
from dotenv import load_dotenv

load_dotenv()

app = FastAPI(title="Task Prediction Service")

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
def startup_event():
    global MODEL
    MODEL = load_model()
    if MODEL is None:
        print("WARNING: Model not found at startup. /predict will return 503 until trained.")
    else:
        print("Model loaded.")

@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    if MODEL is None:
        raise HTTPException(status_code=503, detail="Model not trained")
    payload = req.dict()
    res = predict_from_dict(MODEL, payload)
    return PredictResponse(predicted_minutes=res["predicted_minutes"], confidence=res["confidence"], model_version=os.getenv("MODEL_VERSION", "v1"))

