package com.rm.j2crm.api.controller;

import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.ProjectDto;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.ProjectService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectsController {

  private ProjectService projectService;

  @Autowired
  public ProjectsController (ProjectService projectService) {
    this.projectService = projectService;
  }

  @PostMapping
  public ResponseEntity<ProjectDto> create(@RequestBody ProjectDto project) {
    return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(project));
  }

  @GetMapping
  public ResponseEntity<Page<ProjectDto>> search(
      @RequestBody Optional<Filter> filter,
      @RequestParam Optional<Integer> page,
      @RequestParam Optional<Integer> size,
      @RequestParam Optional<String> sort) {
    return ResponseEntity.ok(projectService.list(filter, page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProjectDto> detail(@PathVariable String id) {
    return ResponseEntity.ok(projectService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectDto> update(
      @PathVariable String id,
      @RequestBody ProjectDto input) {
    return ResponseEntity.ok(projectService.update(id, input));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ProjectDto> updatePatch(
      @PathVariable String id,
      @RequestBody PeriodDto input) {
    return ResponseEntity.ok(projectService.updatePatch(id, input));
  }

  @DeleteMapping("/{id}")
  public  ResponseEntity<Void> delete(@PathVariable String id) {
    projectService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
