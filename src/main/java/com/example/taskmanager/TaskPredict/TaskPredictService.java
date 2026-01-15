package com.example.taskmanager.TaskPredict;

import com.example.taskmanager.TaskPredict.DTO.PredictRequest;
import com.example.taskmanager.TaskPredict.DTO.PredictResponse;
import com.example.taskmanager.TaskPredict.DTO.PredictionDTO;
import com.example.taskmanager.TaskPredict.DTO.TaskPredictViewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskPredictService {

    private final ITaskPredictRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    public TaskPredict requestAndSavePrediction(
            Long taskId,
            String priority,
            String createdAtIso,
            String dueDateIso,
            Long assignedToId,
            Long createdById
    ) {

        PredictRequest req = new PredictRequest(
                priority,
                createdAtIso,
                dueDateIso,
                assignedToId,
                createdById
        );

        PredictResponse resp = restTemplate.postForObject(
                aiServiceUrl + "/predict",
                req,
                PredictResponse.class
        );

        if (resp == null) return null;
        TaskPredict p = TaskPredict.builder()
                .taskId(taskId)
                .priority(priority)
                .estimatedMinutes(resp.predicted_minutes())
                .modelVersion(resp.model_version())
                .assignedToId(assignedToId)
                .createdById(createdById)
                .createdAt(LocalDateTime.now())
                .build();

        return repo.save(p);
    }

    public Optional<PredictionDTO> getLatest(Long taskId) {
        return repo.findTopByTaskIdOrderByCreatedAtDesc(taskId)
                .map(PredictionDTO::from);
    }

    public List<TaskPredictViewDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(TaskPredictViewDTO::from)
                .toList();
    }

    public List<TaskPredictViewDTO> getByTask(Long taskId) {
        return repo.findByTaskId(taskId)
                .stream()
                .map(TaskPredictViewDTO::from)
                .toList();
    }

    public void triggerTraining() {
        restTemplate.postForObject(aiServiceUrl + "/train", null, Void.class);
    }
}
