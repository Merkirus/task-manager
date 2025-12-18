package com.example.taskmanager.TaskPredict.DTO;

import com.example.taskmanager.TaskPredict.TaskPredict;

public record PredictionDTO(
        Integer estimatedMinutes,
        Double confidence,
        String modelVersion
) {
    public static PredictionDTO from(TaskPredict p) {
        if (p == null) return null;
        return new PredictionDTO(
                p.getEstimatedMinutes(),
                null,
                p.getModelVersion()
        );
    }
}

