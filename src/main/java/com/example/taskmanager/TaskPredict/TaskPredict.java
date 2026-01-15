package com.example.taskmanager.TaskPredict;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_predict")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPredict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taskId;

    private Integer estimatedMinutes;

    private Integer realMinutes;

    private String priority;

    private Long assignedToId;

    private Long createdById;

    private LocalDateTime createdAt;

    private String modelVersion;
}
