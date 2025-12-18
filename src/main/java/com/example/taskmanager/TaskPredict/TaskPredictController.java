package com.example.taskmanager.TaskPredict;

import com.example.taskmanager.Task.Dto.*;
import com.example.taskmanager.TaskPredict.DTO.PredictionDTO;
import com.example.taskmanager.TaskPredict.DTO.TaskPredictViewDTO;
import com.example.taskmanager.TaskPredict.TaskPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class TaskPredictController {

    private final TaskPredictService service;


    @GetMapping
    public List<TaskPredictViewDTO> getAll() {
        return service.getAll();
    }


    @GetMapping("/task/{taskId}")
    public List<TaskPredictViewDTO> getByTask(@PathVariable Long taskId) {
        return service.getByTask(taskId);
    }
}

