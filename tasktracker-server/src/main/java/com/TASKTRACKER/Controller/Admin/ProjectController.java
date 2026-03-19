package com.TASKTRACKER.Controller.Admin;

import com.TASKTRACKER.Core.Project;
import com.TASKTRACKER.Service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/Project/create")
    public Project createProject(@Valid @RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping("/Project/read")
    public List<Project> getAllProject() {
        return projectService.getAllProject();
    }

    @GetMapping("/Project/readByName")
    public Project getProjectByName(@RequestParam("name") String name){
        return projectService.getProjectByName(name);
    }

    @PutMapping("/Project/edit/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody Project project) { return projectService.updateProject(id, project); }

    @DeleteMapping("/Project/delete/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
