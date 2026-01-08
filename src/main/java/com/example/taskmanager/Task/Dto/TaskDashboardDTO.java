package com.example.taskmanager.Task.Dto;

import java.util.List;
import java.util.Map;

public record TaskDashboardDTO(
    int totalTasks,
    int pendingTasks,
    int inProgressTasks,
    int completedTasks,
    int overdueTasks,
    Map<String, Integer> taskDistribution,
    Map<String, Integer> taskPriorityLevels,
    List<TaskResponseDTO> recentTasks
) {}
