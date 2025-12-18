package com.example.taskmanager.TaskPredict;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ITaskPredictRepository
        extends JpaRepository<TaskPredict, Long> {

    Optional<TaskPredict>
    findTopByTaskIdOrderByCreatedAtDesc(Long taskId);

    List<TaskPredict> findByTaskId(Long taskId);
}
