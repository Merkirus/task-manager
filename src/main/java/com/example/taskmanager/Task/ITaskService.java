package com.example.taskmanager.Task;

import com.example.taskmanager.Task.Dto.*;
import com.example.taskmanager.User.User;
import jakarta.transaction.Transactional;

import java.util.Collection;

public interface ITaskService {
    @Transactional
    abstract public TaskResponseDTO addTask(TaskCreateDTO task);
    @Transactional
    abstract public Collection<TaskResponseDTO> getTasks();
    abstract public TaskResponseDTO getTask(Long id);
    @Transactional
    abstract public Boolean deleteTask(Long id);
    @Transactional
    abstract public TaskResponseDTO updateTask(Long id, TaskUpdateDTO task);
    @Transactional
    abstract public Task toggleChecklistItem(Long taskId, Long itemId);
    @Transactional
    abstract public Task updateProgress(Long taskId, int value);
    @Transactional
    abstract public Task updateStatus(Long taskId, String status);
    @Transactional
    abstract public Task updateToDoItem(Long taskId, Long itemId, ToDoItemUpdateDTO item);
    @Transactional
    abstract public Task addChecklistItem(Long taskId, ToDoItemUpdateDTO item);
    @Transactional
    abstract public Task deleteChecklistItem(Long taskId, Long itemId);
    @Transactional
    abstract public TaskDashboardDTO getDashboard();
}
