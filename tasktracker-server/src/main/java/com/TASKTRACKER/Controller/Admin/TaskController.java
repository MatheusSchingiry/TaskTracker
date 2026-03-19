package com.TASKTRACKER.Controller.Admin;

import com.TASKTRACKER.Core.Task;
import com.TASKTRACKER.Service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/{projectId}/task/create")
    public Task createTask(@PathVariable Long projectId, @Valid @RequestBody Task task) {
        return taskService.createTask(projectId, task);
    }

    @GetMapping("/{projectId}/task/read")
    public List<Task> getAllTask(@PathVariable Long projectId) {
        return taskService.getAllTasks(projectId);
    }

    @GetMapping("/{projectId}/task/readByName")
    public Task getTaskByName(@PathVariable Long projectId, @RequestParam("name") String name){
        return taskService.getTasksByName(projectId, name);
    }

    @PutMapping("/{projectId}/task/edit/{taskId}")
    public Task updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Task task) {
        return taskService.updateTask(projectId, taskId, task);
    }

    @DeleteMapping("/{projectId}/task/delete/{taskId}")
    public void deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
    }
}
