package com.example.taskmanager.Task;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedById(Long id);
    @Query("""
        select t from Task t
        where t.createdBy.id = :userId
            and t.status <> com.example.taskmanager.Task.Task.Status.COMPLETED
        order by t.dueDate asc
    """)
    List<Task> findRecetTasks(@Param("userId") Long userId, Pageable pageable);
}
