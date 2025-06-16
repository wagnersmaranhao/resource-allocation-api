package com.rm.j2crm.domain.service;

import com.rm.j2crm.domain.dto.AllocationDto;
import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.PositionDto;
import com.rm.j2crm.domain.pageable.Filter;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PositionService {
  Page<PositionDto> list(
      Optional<Filter> filter,
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<String> sort);

  PositionDto getById(String id, String fields);

  PositionDto create(PositionDto position);

  PositionDto update(String id, PositionDto position);

  void remove(String id);

  AllocationDto addResource(String positionId, String resourceId, AllocationDto allocation);

  AllocationDto updateAllocation(String positionId, String resourceId, PeriodDto period);

  void removeResource(String positionId, String resourceId);
}
