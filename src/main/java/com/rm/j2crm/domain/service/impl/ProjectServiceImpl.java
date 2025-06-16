package com.rm.j2crm.domain.service.impl;

import com.rm.j2crm.domain.access.ProjectRepositoryAccess;
import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.ProjectDto;
import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.mapper.ProjectMapper;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.ProjectService;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;
import com.rm.j2crm.domain.util.enums.DateEnumUtil;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

  private ProjectRepositoryAccess projectRepositoryAccess;

  @Autowired
  public ProjectServiceImpl(ProjectRepositoryAccess projectRepositoryAccess) {
    this.projectRepositoryAccess = projectRepositoryAccess;
  }

  @Override
  public Page<ProjectDto> list(
    Optional<Filter> filter, Optional<Integer> page, Optional<Integer> size, Optional<String> sort) {
    log.info("Project list");

    Pageable pageable = PageableUtil.build(page, size, sort, ConstantsUtil.NAME);

    Page<ProjectEntity> projectEntityPage = (filter.isPresent())
      ? projectRepositoryAccess.search(filter.get(), pageable)
      : projectRepositoryAccess.listAll(pageable);

    Page<ProjectDto> projectDtoPage = ProjectMapper.getInstance().map(projectEntityPage);

    return projectDtoPage;
  }

  @Override
  public ProjectDto getById(String id) {
    log.info("Project get by id({})", id);
    return ProjectMapper.getInstance().map(projectRepositoryAccess.findById(id));
  }

  @Override
  public ProjectDto create(ProjectDto project) {
    log.info("Project create, object='{}'", project);
    itIsValid(project, true);
    ProjectEntity entity = projectRepositoryAccess.saveOrUpdate(ProjectMapper.getInstance().map(project));

    log.info("Project created with id '{}'", entity.getId());
    return ProjectMapper.getInstance().map(entity);
  }

  @Override
  public ProjectDto update(String id, ProjectDto project) {
    log.info("Project update by id = '{}' and object = '{}'", id, project);
    itIsValid(project, false);

    ProjectEntity entity = projectRepositoryAccess.findById(id);
    ProjectMapper.getInstance().map(entity, project);

    return ProjectMapper.getInstance().map(projectRepositoryAccess.saveOrUpdate(entity));
  }

  @Override
  public ProjectDto updatePatch(String id, PeriodDto period) {
    log.info("Project update patch with id '{}' and period {}", id, period);
    FunctionsUtil.isValidDate(period.getStartDate(), ConstantsUtil.START_DATE);
    FunctionsUtil.isValidDate(period.getEndDate(), ConstantsUtil.END_DATE);

    Date startDate = FunctionsUtil.stringToDate(period.getStartDate());
    Date endDate = FunctionsUtil.stringToDate(period.getEndDate());

    if (DateEnumUtil.GREATER_EQUAL_THAN.comparedTo(startDate, endDate)) {
      throw new InputDataException(ConstantsUtil.ERROR_END_DATE_GREATER_START_DATE);
    }

    ProjectEntity entity = projectRepositoryAccess.findById(id);
    ProjectMapper.getInstance().map(entity, period);

    return ProjectMapper.getInstance().map(projectRepositoryAccess.saveOrUpdate(entity));
  }

  @Override
  public void remove(String id) {
    log.info("Project remove by id '{}'", id);
    ProjectEntity entity = projectRepositoryAccess.findById(id);
    entity.setIsDeleted(true);

    projectRepositoryAccess.saveOrUpdate(entity);
  }

  private void itIsValid(ProjectDto dto,  boolean isCreate) {
    log.info("Check if input object is valid");
    FunctionsUtil.isValid(dto.getName(), ConstantsUtil.NAME);
    FunctionsUtil.isValid(dto.getDescription(), ConstantsUtil.DESCRIPTION);
    FunctionsUtil.isValidDate(dto.getStartDate(), ConstantsUtil.START_DATE);
    FunctionsUtil.isValidDate(dto.getEndDate(), ConstantsUtil.END_DATE);

    Date dateNow = FunctionsUtil.dateNow();
    Date startDate = FunctionsUtil.stringToDate(dto.getStartDate());
    Date endDate = FunctionsUtil.stringToDate(dto.getEndDate());

    if (DateEnumUtil.GREATER_EQUAL_THAN.comparedTo(startDate, endDate)) {
      throw new InputDataException(ConstantsUtil.ERROR_END_DATE_GREATER_START_DATE);
    }

    if (isCreate && DateEnumUtil.LESS_THAN.comparedTo(startDate, dateNow)) {
      throw new InputDataException(ConstantsUtil.ERROR_START_DATE);
    }
  }
}
