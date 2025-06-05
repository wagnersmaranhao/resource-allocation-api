package com.rm.j2crm.domain.mapper;

import com.rm.j2crm.domain.dto.PositionDto;
import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.util.FunctionsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PositionMapper {
  private static PositionMapper INSTANCE;

  public static synchronized PositionMapper getInstance() {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new PositionMapper();
    }

    return INSTANCE;
  }

  public static PositionDto map(PositionEntity entity) {
    log.info("Position Mapper entity to DTO");
    return PositionDto.builder()
      .id(entity.getId())
      .projectId(entity.getProject().getId())
      .title(entity.getTitle())
      .description(entity.getDescription())
      .role(entity.getRole())
      .numberOfResources(entity.getNumberOfResources())
      .startDate(FunctionsUtil.dateToString(entity.getStartDate()))
      .endDate(FunctionsUtil.dateToString(entity.getEndDate()))
      .status(entity.getStatus())
      .allocations(AllocationMapper.getInstance().map(entity.getAllocations()))
      .build();
  }

  public static PositionEntity map(PositionDto dto) {
    log.info("Position Mapper DTO to entity");
    return PositionEntity.builder()
      .title(dto.getTitle())
      .description(dto.getDescription())
      .role(dto.getRole())
      .numberOfResources(dto.getNumberOfResources())
      .startDate(FunctionsUtil.stringToDate(dto.getStartDate()))
      .endDate(FunctionsUtil.stringToDate(dto.getEndDate()))
      .status(dto.getStatus())
      .allocations(new ArrayList<>())
      .build();
  }

  public List<PositionDto> map(List<PositionEntity> list) {
    return list.stream().map(p -> map(p)).toList();
  }

  public static Page<PositionDto> map(Page<PositionEntity> positionsPage) {
    log.info("Position Mapper list entity to list DTO");
    return positionsPage.map(entity -> map(entity));
  }

  public static void map(PositionEntity targetEntity, PositionDto entity) {
    log.info("Position Mapper target entity to another entity");
    targetEntity.setTitle(entity.getTitle());
    targetEntity.setDescription(entity.getDescription());
    targetEntity.setNumberOfResources(entity.getNumberOfResources());
    targetEntity.setStartDate(FunctionsUtil.stringToDate(entity.getStartDate()));
    targetEntity.setEndDate(FunctionsUtil.stringToDate(entity.getEndDate()));
    targetEntity.setStatus(entity.getStatus());
  }
}
