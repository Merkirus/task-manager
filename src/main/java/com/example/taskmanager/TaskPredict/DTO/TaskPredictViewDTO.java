package com.example.taskmanager.TaskPredict.DTO;

import com.example.taskmanager.TaskPredict.TaskPredict;

import java.time.LocalDateTime;

public record TaskPredictViewDTO(
        Long taskId,
        Integer estimatedMinutes,
        Integer realMinutes,
        Long assignedToId,
        Long createdById,
        String priority,
        String modelVersion,
        LocalDateTime createdAt
) {
    public static TaskPredictViewDTO from(TaskPredict p) {
        return new TaskPredictViewDTO(
                p.getTaskId(),
                p.getEstimatedMinutes(),
                p.getRealMinutes(),
                p.getAssignedToId(),
                p.getCreatedById(),
                p.getPriority(),
                p.getModelVersion(),
                p.getCreatedAt()
        );
    }
}

