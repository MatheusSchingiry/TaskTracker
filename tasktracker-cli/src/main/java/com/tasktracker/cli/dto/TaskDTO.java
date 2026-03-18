package com.tasktracker.cli.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private String status;

    @JsonProperty("createdAt")
    private String createdAt;

    public TaskDTO() {}

    public Long getId()          { return id; }
    public String getNome()      { return title; }
    public String getDescricao() { return description; }
    public String getStatus()    { return status; }
    public String getCriadoEm()  { return createdAt; }

    public void setId(Long id)              { this.id = id; }
    public void setTitle(String title)      { this.title = title; }
    public void setDescription(String desc) { this.description = desc; }
    public void setStatus(String status)    { this.status = status; }
    public void setCreatedAt(String date)   { this.createdAt = date; }
}