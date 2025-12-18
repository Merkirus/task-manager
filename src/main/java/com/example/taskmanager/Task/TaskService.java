package com.example.taskmanager.Task;

import com.example.taskmanager.Task.Dto.*;
import com.example.taskmanager.TaskPredict.DTO.PredictionDTO;
import com.example.taskmanager.TaskPredict.ITaskPredictRepository;
import com.example.taskmanager.TaskPredict.TaskPredict;
import com.example.taskmanager.TaskPredict.TaskPredictService;
import com.example.taskmanager.User.IUserService;
import com.example.taskmanager.User.User;
import com.example.taskmanager.User.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService{
    private final ITaskRepository iTaskRepository;
    private final IUserService iUserService;
    private final TaskPredictService taskPredictService;
    private final ITaskPredictRepository taskPredictRepository;

    private Task getTaskForCurrentUser(Long id) {
        Task task = iTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User current = iUserService.getCurrentUser();

        if (!Objects.equals(task.getCreatedBy().getId(), current.getId())) {
            throw new RuntimeException("Access denied");
        }

        return task;
    }

    @Override
    @Transactional
    public TaskResponseDTO addTask(TaskCreateDTO dto) {
        User current = iUserService.getCurrentUser();


        Task task = Task.builder()
                .title(dto.title())
                .description(dto.description())
                .priority(Task.Priority.valueOf(dto.priority()))
                .status(Task.Status.valueOf(dto.status()))
                .dueDate(dto.dueDate())
                .attachments(dto.attachments())
                .toDoCheckList(dto.toDoCheckList())
                .createdBy(current)
                .progress(0)
                .build();;

        task = iTaskRepository.save(task);


        TaskPredict taskPredict = taskPredictService.requestAndSavePrediction(
                task.getId(),
                task.getPriority().name(),
                task.getCreatedAt().toString(),
                task.getDueDate() != null ? task.getDueDate().toString() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getCreatedBy() != null ? task.getCreatedBy().getId() : null
        );

        PredictionDTO predictionDTO = null;
        if (taskPredict != null) {
            predictionDTO = new PredictionDTO(
                    taskPredict.getEstimatedMinutes(),
                    0.0,
                    taskPredict.getModelVersion()
            );
        }

        return TaskResponseDTO.from(task, predictionDTO);
    }

    @Override
    public Collection<TaskResponseDTO> getTasks() {
        User current = iUserService.getCurrentUser();
        return iTaskRepository.findByCreatedById(current.getId())
                .stream()
                .map(TaskResponseDTO::from)
                .toList();
    }

    @Override
    public TaskResponseDTO getTask(Long id) {
        Task task = iTaskRepository.findById(id).orElse(null);

        Long uid = iUserService.getCurrentUser().getId();

        if (!task.getCreatedBy().getId().equals(uid)) {
            // throw
        }

        return TaskResponseDTO.from(task);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO task) {
        Task old_task = iTaskRepository.findById(id).orElse(null);

        Long uid = iUserService.getCurrentUser().getId();

        if (!old_task.getCreatedBy().getId().equals(uid)) {
            // throw
        }

        old_task.setTitle(task.title());
        old_task.setDescription(task.description());
        old_task.setPriority(Task.Priority.valueOf(task.priority()));
        old_task.setStatus(Task.Status.valueOf(task.status()));
        old_task.setDueDate(task.dueDate());
        old_task.setProgress(task.progress());
        old_task.setAttachments(task.attachments());
        old_task.setToDoCheckList(task.toDoCheckList());

        return TaskResponseDTO.from(iTaskRepository.save(old_task));
    }

    @Override
    @Transactional
    public Boolean deleteTask(Long id) {
        Task old_task = iTaskRepository.getReferenceById(id);
        iTaskRepository.delete(old_task);
        return true;
    }

    @Override
    @Transactional
    public Task toggleChecklistItem(Long taskId, Long itemId) {
        Task task = getTaskForCurrentUser(taskId);

        task.getToDoCheckList().forEach(item -> {
            if (Objects.equals(item.getId(), itemId)) {
                item.setCompleted(!item.isCompleted());
            }
        });
        return iTaskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateProgress(Long taskId, int value) {
        if (value < 0 || value > 100) {
            // exc
        }

        Task task = getTaskForCurrentUser(taskId);

        task.setProgress(value);
        return iTaskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateStatus(Long taskId, String status) {
        Task task = getTaskForCurrentUser(taskId);

        Task.Status newStatus = Task.Status.valueOf(status);
        task.setStatus(newStatus);

        if (newStatus == Task.Status.COMPLETED) {

            taskPredictRepository
                    .findTopByTaskIdOrderByCreatedAtDesc(taskId)
                    .ifPresent(pred -> {

                        long minutes = ChronoUnit.MINUTES.between(
                                task.getCreatedAt().toInstant(),
                                Instant.now()
                        );

                        pred.setRealMinutes((int) minutes);
                        taskPredictRepository.save(pred);
                    });
        }

        return iTaskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateToDoItem(Long taskId, Long itemId, ToDoItemUpdateDTO item) {
        Task task = getTaskForCurrentUser(taskId);

        task.getToDoCheckList().forEach(_item -> {
            if (Objects.equals(_item.getId(), itemId)) {
                _item.setText(item.text());
            }
        });
        return iTaskRepository.save(task);
    }

    @Override
    @Transactional
    public Task addChecklistItem(Long taskId, ToDoItemUpdateDTO item) {
        Task task = getTaskForCurrentUser(taskId);

        Task.ToDoItem _item = new Task.ToDoItem();
        _item.setText(item.text());
        _item.setCompleted(false);

        task.getToDoCheckList().add(_item);
        return iTaskRepository.save(task);
    }

    @Override
    @Transactional
    public Task deleteChecklistItem(Long taskId, Long itemId) {
        Task task = getTaskForCurrentUser(taskId);

        task.getToDoCheckList().removeIf(
                item -> Objects.equals(item.getId(), itemId)
        );

        return iTaskRepository.save(task);
    }

    @Override
    @Transactional
    public TaskDashboardDTO getDashboard() {
        User current = iUserService.getCurrentUser();

        List<Task> tasks = iTaskRepository.findByCreatedById(current.getId());

        int total = tasks.size();
        int pending = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.PENDING).count();
        int completed = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.COMPLETED).count();
        int inProgress = (int) tasks.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        int overdue = (int) tasks.stream().filter(t -> t.getDueDate().before(new Date())).count();

        Map<String, Integer> distribution = Map.of(
                "PENDING", pending,
                "IN_PROGRESS", inProgress,
                "COMPLETED", completed,
                "OVERDUE", overdue,
                "ALL", total
        );

        Map<String, Integer> priorityLevels = Map.of(
                "LOW", (int) tasks.stream().filter(t -> t.getPriority() == Task.Priority.LOW).count(),
                "MEDIUM", (int) tasks.stream().filter(t -> t.getPriority() == Task.Priority.MEDIUM).count(),
                "HIGH", (int) tasks.stream().filter(t -> t.getPriority() == Task.Priority.HIGH).count()
        );

        return new TaskDashboardDTO(total, pending, inProgress, completed, overdue, distribution, priorityLevels);
    }
    private final ITaskRepository taskRepository;
    @Override
    public Task addTaskEntity(TaskCreateDTO dto) {
        Task task = Task.builder()
                .title(dto.title())
                .description(dto.description())
                .priority(Task.Priority.valueOf(dto.priority()))
                .status(Task.Status.PENDING)
                .createdAt(new Date())
                .dueDate(dto.dueDate())
                .build();

        return taskRepository.save(task);
    }



}
