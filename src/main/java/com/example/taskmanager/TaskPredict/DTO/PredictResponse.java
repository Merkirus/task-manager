package com.example.taskmanager.TaskPredict.DTO;

public record PredictResponse(
        int predicted_minutes,
        double confidence,
        String model_version
) {}
