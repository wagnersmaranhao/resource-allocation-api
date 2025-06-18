package com.rm.j2crm.domain.mapper;

import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.ProjectDto;
import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.util.FunctionsUtil;

import java.util.ArrayList;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectMapper {

  private static ProjectMapper INSTANCE;

  public static synchronized ProjectMapper getInstance() {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new ProjectMapper();
    }

    return INSTANCE;
  }

  public static ProjectDto map(ProjectEntity entity) {
    log.info("Project Mapper entity to DTO");
    return ProjectDto.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .status(entity.getStatus())
      .startDate(FunctionsUtil.dateToString(entity.getStartDate()))
      .endDate(FunctionsUtil.dateToString(entity.getEndDate()))
      .positions(PositionMapper.getInstance().map(entity.getPositions()))
      .build();
  }

  public static ProjectEntity map(ProjectDto dto) {
    log.info("Project Mapper DTO to entity");
    return ProjectEntity.builder()
      .name(dto.getName())
      .description(dto.getDescription())
      .status(dto.getStatus())
      .startDate(FunctionsUtil.stringToDate(dto.getStartDate()))
      .endDate(FunctionsUtil.stringToDate(dto.getEndDate()))
      .positions(new ArrayList<>())
      .build();
  }

  public static Page<ProjectDto> map(Page<ProjectEntity> projectsPage) {
    log.info("Project Mapper list entity to list DTO");
    return projectsPage.map(entity -> map(entity));
  }

  public static void map(ProjectEntity targetEntity, ProjectDto entity) {
    log.info("Project Mapper target entity to another entity");
    targetEntity.setName(entity.getName());
    targetEntity.setDescription(entity.getDescription());
    targetEntity.setStatus(entity.getStatus());
    targetEntity.setStartDate(FunctionsUtil.stringToDate(entity.getStartDate()));
    targetEntity.setEndDate(FunctionsUtil.stringToDate(entity.getEndDate()));
  }

  public static void map(ProjectEntity targetEntity, PeriodDto period) {
    log.info("Project Mapper target entity to another DTO");
    targetEntity.setStartDate(FunctionsUtil.stringToDate(period.getStartDate()));
    targetEntity.setEndDate(FunctionsUtil.stringToDate(period.getEndDate()));
  }
}
