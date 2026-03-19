package com.TASKTRACKER.Controller.Public;

import com.TASKTRACKER.Core.Project;
import com.TASKTRACKER.Core.Task;
import com.TASKTRACKER.Service.ProjectService;
import com.TASKTRACKER.Service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "*")
public class PublicController {

    private final ProjectService projectService;
    private final TaskService taskService;

    public PublicController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping("/Project/read")
    public List<Project> getAllProjectPublic() {
        return projectService.getAllProjectPublic();
    }

    @GetMapping("/Project/readByName")
    public Project getProjectByNamePublic(@RequestParam("name") String name){ return projectService.getProjectByName(name); }

    @GetMapping("/{projectId}/task/read")
    public List<Task> getAllTaskPublic(@PathVariable Long projectId) {
        return taskService.getAllTasksPublic(projectId);
    }

    @GetMapping("/{projectId}/task/readByName")
    public Task getTaskByNamePublic(@PathVariable Long projectId, @RequestParam("name") String name){
        return taskService.getTaskByNamePublic(projectId, name);
    }
}
