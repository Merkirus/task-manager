package com.example.taskmanager.TaskPredict;

import com.example.taskmanager.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ITaskPredictRepository
        extends JpaRepository<TaskPredict, Long> {

    Optional<TaskPredict>
    findTopByTaskIdOrderByCreatedAtDesc(Long taskId);

    List<TaskPredict> findByTaskId(Long taskId);

    @Query("""
        select tp
        from TaskPredict tp
        join Task t on t.id = tp.taskId
        where t.createdBy.id = :userId
        order by tp.createdAt desc
    """)
    List<TaskPredict> findPredictionsForUser(@Param("userId") Long userId);
}

