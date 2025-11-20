package com.example.taskmanager.Task;

import com.example.taskmanager.Task.Dto.TaskCreateDTO;
import com.example.taskmanager.Task.Dto.TaskResponseDTO;
import com.example.taskmanager.Task.Dto.TaskUpdateDTO;
import com.example.taskmanager.Task.Dto.ToDoItemUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/tasks")
@RestController
public class TaskController {
    private final ITaskService iTaskService;

    public TaskController(ITaskService iTaskService) {
        this.iTaskService = iTaskService;
    }

    @GetMapping
    public ResponseEntity<Collection<TaskResponseDTO>> getTasks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.getTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.getTask(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> addTask(@RequestBody TaskCreateDTO task) {
        return  ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.addTask(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable("id") Long id, @RequestBody TaskUpdateDTO task) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteTask(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.deleteTask(id));
    }

    @PatchMapping("/{taskId}/checklist/{itemId}/toggle")
    public ResponseEntity<Task> toggleChecklistItem(@PathVariable("taskId") Long taskId, @PathVariable("itemId") Long itemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.toggleChecklistItem(taskId, itemId));
    }

    @PatchMapping("/{taskId}/progress/{value}")
    public ResponseEntity<Task> updateTaskProgess(@PathVariable("taskId") Long taskId, @PathVariable("value") Integer value) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.updateProgress(taskId, value));
    }

    @PatchMapping("/{taskId}/status/{status}")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable("taskId") Long taskId, @PathVariable("status") String status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.updateStatus(taskId, status));
    }

    @PatchMapping("/{taskId}/checklist/{itemId}")
    public ResponseEntity<Task> updateChecklistItem(@PathVariable("taskId") Long taskId, @PathVariable("itemId") Long itemId, @RequestBody ToDoItemUpdateDTO item) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.updateToDoItem(taskId, itemId, item));
    }

    @PostMapping("/{taskId}/checklist")
    public ResponseEntity<Task> addChecklistItem(@PathVariable("taskId") Long taskId, @RequestBody ToDoItemUpdateDTO item) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.addChecklistItem(taskId, item));
    }

    @DeleteMapping("/{taskId}/checklist/{itemId}")
    public ResponseEntity<Task> deleteChecklistItem(@PathVariable("taskId") Long taskId, @PathVariable("itemId") Long itemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(iTaskService.deleteChecklistItem(taskId, itemId));
    }
}
