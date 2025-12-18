package com.example.taskmanager.Task.Dto;

import com.example.taskmanager.Task.Task;
import com.example.taskmanager.Task.Task.ToDoItem;
import com.example.taskmanager.TaskPredict.DTO.PredictionDTO;
import com.example.taskmanager.TaskPredict.TaskPredict;

import java.util.Date;
import java.util.List;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        String priority,
        String status,
        Date dueDate,
        Integer progress,
        PredictionDTO prediction,
        List<String> attachments,
        List<ToDoItem> toDoCheckList,
        String assignedTo,
        Long assignedToId,
        String createdBy,
        Long createdById,
        Date createdAt
) {
    public static TaskResponseDTO from(Task task) {
        return from(task, null);
    }

    public static TaskResponseDTO from(Task task, PredictionDTO prediction) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority().name(),
                task.getStatus().name(),
                task.getDueDate(),
                task.getProgress(),
                prediction,
                task.getAttachments(),
                task.getToDoCheckList(),
                task.getAssignedTo() != null ? task.getAssignedTo().getName() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getCreatedBy().getName(),
                task.getCreatedBy() != null ? task.getCreatedBy().getId() : null,
                task.getCreatedAt()
        );
    }
}
