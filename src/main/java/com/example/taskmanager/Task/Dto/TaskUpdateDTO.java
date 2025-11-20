package com.example.taskmanager.Task.Dto;

import com.example.taskmanager.Task.Task.ToDoItem;

import java.util.Date;
import java.util.List;

public record TaskUpdateDTO(
        String title,
        String description,
        String priority,
        String status,
        Date dueDate,
        Integer progress,
        List<String> attachments,
        List<ToDoItem> toDoCheckList
) {}
