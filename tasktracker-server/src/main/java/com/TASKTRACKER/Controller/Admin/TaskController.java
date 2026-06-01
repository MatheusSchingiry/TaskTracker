package com.TASKTRACKER.Controller.Admin;

import com.TASKTRACKER.Core.Task;
import com.TASKTRACKER.Service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/{projectId}/task/create")
    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public Task createTask(@PathVariable Long projectId, @Valid @RequestBody Task task) {
        return taskService.createTask(projectId, task);
    }

    @GetMapping("/{projectId}/task/read")
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public List<Task> getAllTask(@PathVariable Long projectId) {
        return taskService.getAllTasks(projectId);
    }

    @GetMapping("/{projectId}/task/readByName")
    @Operation(summary = "Get task by name")
    @ApiResponse(responseCode = "200", description = "Task found successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Task getTaskByName(@PathVariable Long projectId, @RequestParam("name") String name){
        return taskService.getTasksByName(projectId, name);
    }

    @PutMapping("/{projectId}/task/edit/{taskId}")
    @Operation(summary = "Update a task")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public Task updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Task task) {
        return taskService.updateTask(projectId, taskId, task);
    }

    @DeleteMapping("/{projectId}/task/delete/{taskId}")
    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "200", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public void deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
    }
}
