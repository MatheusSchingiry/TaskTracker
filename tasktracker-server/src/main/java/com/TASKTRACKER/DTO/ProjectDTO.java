package com.TASKTRACKER.DTO;

import com.TASKTRACKER.Core.Enum.ProjectStatus;
import com.TASKTRACKER.Core.Project;
import com.TASKTRACKER.Core.Task;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private String technology;
    private List<Task> tasks;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private int totalTasks;

    public static ProjectDTO from(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.id         = project.getId();
        dto.name       = project.getName();
        dto.status     = project.getStatus();
        dto.technology = project.getTechnology();
        dto.createdAt   = project.getCreatedAt();
        dto.totalTasks = project.getTasks().size();
        return dto;
    }

    public ProjectDTO() {
    }

    public ProjectDTO(Long id, String name, String description, String technology, List<Task> tasks, ProjectStatus status, LocalDateTime createdAt, int totalTasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.technology = technology;
        this.tasks = tasks;
        this.status = status;
        this.createdAt = createdAt;
        this.totalTasks = totalTasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
}
