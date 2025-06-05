package com.rm.j2crm.api.controller;

import com.rm.j2crm.domain.dto.ResourceDto;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.ResourceService;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
public class ResourcesController {

  private ResourceService resourceService;

  public ResourcesController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @PostMapping
  public ResponseEntity<ResourceDto> create(@RequestBody ResourceDto resource) {
    return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.create(resource));
  }

  @GetMapping
  public ResponseEntity<Page<ResourceDto>> search(
      @RequestBody Optional<Filter> filter,
      @RequestParam Optional<Integer> page,
      @RequestParam Optional<Integer> size,
      @RequestParam Optional<String> sort) {
    return ResponseEntity.ok(resourceService.list(filter, page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceDto> detail(
      @PathVariable String id, @RequestParam(required = false) String fields) {
    return ResponseEntity.ok(resourceService.getById(id, fields));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResourceDto> update(
      @PathVariable String id, @RequestBody ResourceDto input) {
    return ResponseEntity.ok(resourceService.update(id, input));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    resourceService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/{id}/cv")
  public ResponseEntity<ResourceDto> upload(@PathVariable String id, @RequestBody MultipartFile file) {
    resourceService.upload(id, file);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/{id}/cv")
  public ResponseEntity<Resource> download(@PathVariable String id) {
    return ResponseEntity.ok(resourceService.download(id));
  }
}
