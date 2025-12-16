package com.example.taskmanager.TaskPredict;

import com.example.taskmanager.TaskPredict.DTO.PredictRequest;
import com.example.taskmanager.TaskPredict.DTO.PredictResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskPredictService {

    private final ITaskPredictRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;


    public TaskPredict requestAndSavePrediction(Long taskId,
                                                String priority,
                                                String createdAtIso,
                                                String dueDateIso,
                                                Long assignedToId,
                                                Long createdById) {


        PredictRequest req = new PredictRequest(priority, createdAtIso, dueDateIso, assignedToId, createdById);
        String url = aiServiceUrl + "/predict";

        PredictResponse resp = restTemplate.postForObject(url, req, PredictResponse.class);

        assert resp != null;
        TaskPredict p = TaskPredict.builder()
                .taskId(taskId)
                .estimatedMinutes(resp.predicted_minutes())
                .priority(priority)
                .assignedToId(assignedToId)
                .createdById(createdById)
                .createdAt(LocalDateTime.now())
                .modelVersion(resp.model_version())
                .build();

        return repo.save(p);
    }
}
