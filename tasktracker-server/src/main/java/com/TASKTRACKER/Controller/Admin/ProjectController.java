package com.TASKTRACKER.Controller.Admin;

import com.TASKTRACKER.Core.Project;
import com.TASKTRACKER.Service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/Project/create")
    @Operation(summary = "Create a new project")
    @ApiResponse(responseCode = "201", description = "Project created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public Project createProject(@Valid @RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping("/Project/read")
    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    public List<Project> getAllProject() {
        return projectService.getAllProject();
    }

    @GetMapping("/Project/readByName")
    @Operation(summary = "Get project by name")
    @ApiResponse(responseCode = "200", description = "Project found successfully")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public Project getProjectByName(@RequestParam("name") String name){
        return projectService.getProjectByName(name);
    }

    @PutMapping("/Project/edit/{id}")
    @Operation(summary = "Update a project")
    @ApiResponse(responseCode = "200", description = "Project updated successfully")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public Project updateProject(@PathVariable Long id, @RequestBody Project project) { return projectService.updateProject(id, project); }

    @DeleteMapping("/Project/delete/{id}")
    @Operation(summary = "Delete a project")
    @ApiResponse(responseCode = "200", description = "Project deleted successfully")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
