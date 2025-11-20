package com.example.taskmanager.Task;

import com.example.taskmanager.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ElementCollection
    private List<String> attachments;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ToDoItem> toDoCheckList;

    private Integer progress = 0;

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { PENDING, IN_PROGRESS, COMPLETED }

    @Entity
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToDoItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String text;

        private boolean completed = false;
    }
}
