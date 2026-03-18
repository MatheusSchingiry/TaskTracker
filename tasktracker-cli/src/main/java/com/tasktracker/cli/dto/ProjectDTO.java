package com.tasktracker.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private String technology;
    private String status;

    @JsonProperty("createdAt")
    private String createdAt;

    private List<Object> tasks;

    public ProjectDTO() {}

    public Long getId()           { return id; }
    public String getNome()       { return name; }
    public String getDescricao()  { return description; }
    public String getTechnology() { return technology; }
    public String getStatus()     { return status; }
    public String getCriadoEm()   { return createdAt; }
    public int getTotalTasks()    { return tasks != null ? tasks.size() : 0; }

    public void setId(Long id)                  { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setTechnology(String tech)      { this.technology = tech; }
    public void setStatus(String status)        { this.status = status; }
    public void setCreatedAt(String createdAt)  { this.createdAt = createdAt; }
    public void setTasks(List<Object> tasks)    { this.tasks = tasks; }
}
