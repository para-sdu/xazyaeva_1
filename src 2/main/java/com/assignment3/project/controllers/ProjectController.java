package com.assignment3.project.controllers;

import com.assignment3.project.dto.requests.ProjectCreateRequest;
import com.assignment3.project.dto.requests.ProjectUpdateRequest;
import com.assignment3.project.dto.responses.DonationResponse;
import com.assignment3.project.dto.responses.ProjectResponse;
import com.assignment3.project.entities.User;
import com.assignment3.project.enums.UserRole;
import com.assignment3.project.services.DonationService;
import com.assignment3.project.services.ProjectService;
import com.assignment3.project.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final DonationService donationService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<ProjectResponse>> getAllProjectsForAdmin() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only administrators can access all projects");
        }
        List<ProjectResponse> projects = projectService.getAllProjectsForAdmin();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable("id") Long projectId
    ) {
        ProjectResponse project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(project);
    }

    @PostMapping()
    public ResponseEntity<ProjectResponse> createProject(
            @RequestBody @Valid ProjectCreateRequest request
    ) {
        ProjectResponse draft = projectService.createProject(request);
        return ResponseEntity.ok(draft);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable("id") Long projectId,
            @RequestBody @Valid ProjectUpdateRequest projectToUpdate
    ) {
        ProjectResponse updatedProject = projectService.updateProject(projectId, projectToUpdate);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable("id") Long projectId
    ) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProjectResponse>> filterProjects(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String text
    ){
        List<ProjectResponse> filteredProjects = projectService.filterProjects(categoryId, text);
        return ResponseEntity.ok(filteredProjects);
    }

    @GetMapping("/{id}/donations")
    public ResponseEntity<List<DonationResponse>> getProjectDonations(@PathVariable("id") Long projectId) {
        List<DonationResponse> donations = donationService.getDonationsByProjectId(projectId);
        return ResponseEntity.ok(donations);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<ProjectResponse> verifyProject(@PathVariable("id") Long projectId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only administrators can verify projects");
        }
        
        ProjectResponse verifiedProject = projectService.verifyProject(projectId);
        return ResponseEntity.ok(verifiedProject);
    }

}
