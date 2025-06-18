package com.rm.j2crm.api.controller;

import com.rm.j2crm.domain.dto.AllocationDto;
import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.PositionDto;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.PositionService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/position")
public class PositionsController {

  private PositionService positionService;

  @Autowired
  public PositionsController(PositionService positionService) {
    this.positionService = positionService;
  }

  @PostMapping
  public ResponseEntity<PositionDto> create(@RequestBody PositionDto position) {
    return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(position));
  }

  @GetMapping
  public ResponseEntity<Page<PositionDto>> search(
          @RequestBody Optional<Filter> filter,
          @RequestParam Optional<Integer> page,
          @RequestParam Optional<Integer> size,
          @RequestParam Optional<String> sort) {
    return ResponseEntity.ok(positionService.list(filter, page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PositionDto> detail(@PathVariable String id) {
    return ResponseEntity.ok(positionService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PositionDto> update(
          @PathVariable String id, @RequestBody PositionDto input) {
    return ResponseEntity.ok(positionService.update(id, input));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    positionService.remove(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/{id}/resource/{resourceId}")
  public ResponseEntity<AllocationDto> addResource(
      @PathVariable String id,
      @PathVariable String resourceId,
      @RequestBody AllocationDto allocation) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      positionService.addResource(id, resourceId, allocation));
  }

  @PatchMapping("/{id}/resource/{resourceId}")
  public ResponseEntity<AllocationDto> updateResource(
          @PathVariable String id,
          @PathVariable String resourceId,
          @RequestBody PeriodDto period) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      positionService.updateAllocation(id, resourceId, period));
  }

  @DeleteMapping("/{id}/resource/{resourceId}")
  public ResponseEntity<Void> deleteResource(@PathVariable String id, @PathVariable String resourceId) {
    positionService.removeResource(id, resourceId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
