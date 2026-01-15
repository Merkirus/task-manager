package com.example.taskmanager.Task;

import com.example.taskmanager.User.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tasks")
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
    @CollectionTable(name = "task_attachments", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "attachment")
    private List<String> attachments;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "task_to_do_check_list",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "to_do_check_list_id")
    )
    private List<ToDoItem> toDoCheckList;

    private Integer progress = 0;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { PENDING, IN_PROGRESS, COMPLETED }

    @Entity
    @Table(name = "to_do_item")
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
