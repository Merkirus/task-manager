package com.example.taskmanager.TaskPredict.DTO;

public record PredictRequest(
        String priority,
        String created_at,
        String due_date,
        Long assigned_to_id,
        Long created_by_id
) {}
