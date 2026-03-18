package com.TASKTRACKER.Service;

import com.TASKTRACKER.Core.Enum.ProjectStatus;
import com.TASKTRACKER.Core.Project;
import com.TASKTRACKER.Repository.ProjectRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @CacheEvict(value = "projects", allEntries = true)
    public Project createProject(Project project) {
        project.setStatus(ProjectStatus.ACTIVE);
        return projectRepository.save(project);
    }

    public List<Project> getAllProject() {
        return projectRepository.findAll();
    }

    public Project getProjectByName(String name) {
        return projectRepository.findByName(name).orElseThrow(() -> new RuntimeException("Project with name " + name + " not found"));
    }

    @CacheEvict(value = "projects", allEntries = true)
    public Project updateProject(Long id, Project project) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project with id " + id + " not found"));

        if (project.getName() != null) { existingProject.setName(project.getName()); }
        if (project.getDescription() != null) { existingProject.setDescription(project.getDescription()); }
        if (project.getTechnology() != null) { existingProject.setTechnology(project.getTechnology()); }
        if (project.getStatus() != null) { existingProject.setStatus(project.getStatus()); }

        return projectRepository.save(existingProject);
    }

    @CacheEvict(value = "projects", allEntries = true)
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Cacheable(value = "projects", key = "'all'")
    public List<Project> getAllProjectPublic() {
        return projectRepository.findAll().stream().filter(project -> project.getStatus() == ProjectStatus.ACTIVE).toList();
    }

    @Cacheable(value = "projects", key = "#name")
    public Project getProjectByNamePublic(String name) {
        return projectRepository.findByName(name)
                .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Project with name " + name + " not found or is not active"));
    }
}
