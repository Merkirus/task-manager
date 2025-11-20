package com.example.taskmanager.Task.Dto;

import com.example.taskmanager.Task.Task.ToDoItem;

import java.util.Date;
import java.util.List;

public record TaskCreateDTO(
        String title,
        String description,
        String priority,
        String status,
        Date dueDate,
        List<String> attachments,
        List<ToDoItem> toDoCheckList
) {}
