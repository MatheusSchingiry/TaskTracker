package com.TASKTRACKER.Service;

import com.TASKTRACKER.Core.Enum.TaskStatus;
import com.TASKTRACKER.Core.Project;
import com.TASKTRACKER.Core.Task;
import com.TASKTRACKER.Repository.ProjectRepository;
import com.TASKTRACKER.Repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "'project:' + #projectId + ':all'")
    public Task createTask(Long projectId, Task task) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        task.setProject(project);
        task.setStatus(TaskStatus.PENDING);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTasksByName(Long projectId, String name) {
        return taskRepository.findByTitle(name)
                .filter(t -> t.getProject().getId().equals(projectId))
                .orElseThrow(() -> new RuntimeException("Task with name " + name + " not found in this project"));
    }

    @Transactional
    @CacheEvict(value = "tasks", allEntries = true)
    public Task updateTask(Long projectId, Long taskId, Task taskDetails) {
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getProject().getId().equals(projectId))
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));

        if (taskDetails.getTitle() != null) task.setTitle(taskDetails.getTitle());
        if (taskDetails.getDescription() != null) task.setDescription(taskDetails.getDescription());
        if (taskDetails.getStatus() != null) task.setStatus(taskDetails.getStatus());

        return taskRepository.save(task);
    }

    @Transactional
    @CacheEvict(value = "tasks", key = "'project:' + #projectId + ':all'")
    public void deleteTask(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getProject().getId().equals(projectId))
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));
        taskRepository.delete(task);
    }


    @Cacheable(value = "tasks", key = "'project:' + #projectId + ':all'")
    public List<Task> getAllTasksPublic(Long projectId) {
        return taskRepository.findAll()
                .stream()
                .filter(t -> t.getProject().getId().equals(projectId) && t.getStatus() == TaskStatus.IN_PROGRESS)
                .toList();
    }

    @Cacheable(value = "tasks", key = "'project:' + #projectId + ':name:' + #name")
    public Task getTaskByNamePublic(Long projectId, String name) {
        return taskRepository.findByTitle(name)
                .filter(t -> t.getProject().getId().equals(projectId) && t.getStatus() == TaskStatus.IN_PROGRESS)
                .orElseThrow(() -> new RuntimeException("Task with name " + name + " not found or is not active in this project"));
    }
}