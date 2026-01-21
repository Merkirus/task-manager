package com.example.taskmanager.TaskPredict.DTO;

public record PredictResponse(
        int predicted_minutes,
        String model_version
) {}
