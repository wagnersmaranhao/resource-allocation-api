package com.rm.j2crm.domain.service.impl;

import com.rm.j2crm.domain.access.AllocationRepositoryAccess;
import com.rm.j2crm.domain.access.PositionRepositoryAccess;
import com.rm.j2crm.domain.access.ProjectRepositoryAccess;
import com.rm.j2crm.domain.access.ResourceRepositoryAccess;
import com.rm.j2crm.domain.dto.AllocationDto;
import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.PositionDto;
import com.rm.j2crm.domain.entity.AllocationEntity;
import com.rm.j2crm.domain.entity.AllocationEntityId;
import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.enums.PositionStatusEnum;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.mapper.AllocationMapper;
import com.rm.j2crm.domain.mapper.PositionMapper;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.PositionService;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;
import com.rm.j2crm.domain.util.enums.DateEnumUtil;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PositionServiceImpl implements PositionService {

  private PositionRepositoryAccess positionRepositoryAccess;
  private ProjectRepositoryAccess projectRepositoryAccess;
  private ResourceRepositoryAccess resourceRepositoryAccess;
  private AllocationRepositoryAccess allocationRepositoryAccess;

  @Autowired
  public PositionServiceImpl(
    PositionRepositoryAccess positionRepositoryAccess,
    ProjectRepositoryAccess projectRepositoryAccess,
    ResourceRepositoryAccess resourceRepositoryAccess,
    AllocationRepositoryAccess allocationRepositoryAccess) {
    this.positionRepositoryAccess = positionRepositoryAccess;
    this.projectRepositoryAccess = projectRepositoryAccess;
    this.resourceRepositoryAccess = resourceRepositoryAccess;
    this.allocationRepositoryAccess = allocationRepositoryAccess;
  }

  @Override
  public Page<PositionDto> list(
      Optional<Filter> filter,
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<String> sort) {
    log.info("Position list");

    Pageable pageable = PageableUtil.build(page, size, sort, ConstantsUtil.TITLE);

    Page<PositionEntity> positionEntityPage =
      (filter.isPresent())
        ? positionRepositoryAccess.search(filter.get(), pageable)
        : positionRepositoryAccess.listAll(pageable);

    Page<PositionDto> projectDtoPage = PositionMapper.getInstance().map(positionEntityPage);
    return projectDtoPage;
  }

  @Override
  public PositionDto getById(String id) {
    log.info("Position get by id({})", id);
    return PositionMapper.getInstance().map(positionRepositoryAccess.findById(id));
  }

  @Override
  @Transactional
  public PositionDto create(PositionDto positionDto) {
    log.info("Position create, object='{}'", positionDto);
    itIsValid(positionDto);

    var ref = new Object() {
      PositionEntity positionEntity = PositionMapper.getInstance().map(positionDto);
    };

    ref.positionEntity.setProject(projectRepositoryAccess.findById(positionDto.getProjectId()));

    if (Objects.nonNull(positionDto.getAllocations()) && positionDto.getAllocations().size() > 0) {
      ref.positionEntity.setStatus(PositionStatusEnum.FILLED);

      positionDto.getAllocations().stream()
        .forEach(allocation -> addAllocation(ref.positionEntity, allocation));
    }
    else {
      ref.positionEntity.setStatus(PositionStatusEnum.OPEN);
    }

    ref.positionEntity = positionRepositoryAccess.saveOrUpdate(ref.positionEntity);
    allocationRepositoryAccess.saveAll(ref.positionEntity.getAllocations());

    log.info("Position created with id '{}'", ref.positionEntity.getId());
    return PositionMapper.getInstance().map(ref.positionEntity);
  }

  @Override
  @Transactional
  public PositionDto update(String id, PositionDto position) {
    log.info("Position update by id = '{}' and object = '{}'", id, position);
    itIsValid(position);

    var ref = new Object() {
      PositionEntity entity = positionRepositoryAccess.findById(id);
    };

    PositionMapper.getInstance().map(ref.entity, position);

    if (Objects.nonNull(position.getAllocations()) && position.getAllocations().size() > 0) {
      ref.entity.setStatus(PositionStatusEnum.FILLED);

      position.getAllocations().stream()
        .forEach(allocation -> createOrUpdateAllocation(ref.entity, allocation));
    }

    //Remover(IsDeleted = true) os Allocations do position que não estão no input.
    removeAllocation(ref.entity, position);

    ref.entity = positionRepositoryAccess.saveOrUpdate(ref.entity);
    allocationRepositoryAccess.saveAll(ref.entity.getAllocations());

    ref.entity.getAllocations().removeIf(p -> p.getIsDeleted().equals(true));

    return PositionMapper.getInstance().map(ref.entity);
  }

  @Override
  public void remove(String id) {
    log.info("Position remove by id '{}'", id);
    PositionEntity entity = positionRepositoryAccess.findById(id);
    entity.setIsDeleted(true);

    positionRepositoryAccess.saveOrUpdate(entity);
  }

  @Override
  @Transactional
  public AllocationDto addResource(String positionId, String resourceId, AllocationDto allocation) {
    log.info("Position add resource by id '{}'", positionId);
    itIsValid(allocation);

    PositionEntity entity = positionRepositoryAccess.findById(positionId);

    if (entity.getAllocations().stream()
      .anyMatch(p -> p.getResource().getId().equals(resourceId))) {
       throw new InputDataException("Resource already exists.");
    }

    entity.setStatus(PositionStatusEnum.FILLED);
    entity = positionRepositoryAccess.saveOrUpdate(entity);

    allocation.setPositionId(positionId);
    allocation.setResourceId(resourceId);

    ResourceEntity resource = resourceRepositoryAccess.findById(allocation.getResourceId());
    AllocationEntity tempAllocEntity = AllocationMapper.getInstance().map(allocation, entity, resource);
    tempAllocEntity = allocationRepositoryAccess.saveOrUpdate(tempAllocEntity, null);

    return AllocationMapper.getInstance().map(tempAllocEntity);
  }

  @Override
  public AllocationDto updateAllocation(String positionId, String resourceId, PeriodDto period) {
    log.info("Position update resource by id '{}'", positionId);
    AllocationDto dto = AllocationMapper.getInstance().map(period);
    itIsValid(dto);

    AllocationEntity entity = allocationRepositoryAccess.findById(
      AllocationEntityId.builder().positionId(positionId).resourceId(resourceId).build());

    AllocationMapper.getInstance().map(entity, dto);

    entity = allocationRepositoryAccess.saveOrUpdate(entity, null);
    return AllocationMapper.getInstance().map(entity);
  }

  @Override
  public void removeResource(String positionId, String resourceId) {
    log.info("Position remove resource by id '{}'", positionId);
    AllocationEntity entity = allocationRepositoryAccess.findById(
            AllocationEntityId.builder().positionId(positionId).resourceId(resourceId).build());
    entity.setIsDeleted(true);
    entity.setEndDate(new Date());
    allocationRepositoryAccess.saveOrUpdate(entity, null);
  }

  private void createOrUpdateAllocation(PositionEntity entity, AllocationDto allocation) {
    if (entity.getAllocations().isEmpty()) {
      addAllocation(entity, allocation);
    }
    else {
      entity.getAllocations().stream()
        .filter(p -> p.getResource().getId().equals(allocation.getResourceId()))
        .forEach(v -> {
          AllocationMapper.getInstance().map(v, allocation);
          v.setIsDeleted(false);
        });
    }
  }

  private void addAllocation(PositionEntity entity, AllocationDto allocation) {
    ResourceEntity resource = resourceRepositoryAccess.findById(allocation.getResourceId());
    entity.addResource(allocation, resource);
  }

  private void removeAllocation(PositionEntity entity, PositionDto position) {
    entity.getAllocations().stream().filter(p ->
      position.getAllocations().stream()
        .noneMatch(m -> m.getResourceId().equals(p.getResource().getId()))
    ).forEach(v -> v.setIsDeleted(true));
  }

  private void itIsValid(AllocationDto dto) {
    FunctionsUtil.isValidDate(dto.getStartDate(), ConstantsUtil.START_DATE);
    FunctionsUtil.isValidDate(dto.getEndDate(), ConstantsUtil.END_DATE);

    Date startDate = FunctionsUtil.stringToDate(dto.getStartDate());
    Date endDate = FunctionsUtil.stringToDate(dto.getEndDate());

    if (DateEnumUtil.GREATER_EQUAL_THAN.comparedTo(startDate, endDate)) {
      throw new InputDataException(ConstantsUtil.ERROR_END_DATE_GREATER_START_DATE);
    }
  }

  private void itIsValid(PositionDto dto) {
    log.info("Check if input object is valid");
    FunctionsUtil.isValid(dto.getTitle(), ConstantsUtil.TITLE);
    FunctionsUtil.isValid(dto.getDescription(), ConstantsUtil.DESCRIPTION);
    FunctionsUtil.isValid(dto.getRole(), ConstantsUtil.ROLE);
    FunctionsUtil.isValidDate(dto.getStartDate(), ConstantsUtil.START_DATE);
    FunctionsUtil.isValidDate(dto.getEndDate(), ConstantsUtil.END_DATE);

    Date startDate = FunctionsUtil.stringToDate(dto.getStartDate());
    Date endDate = FunctionsUtil.stringToDate(dto.getEndDate());

    if (DateEnumUtil.GREATER_EQUAL_THAN.comparedTo(startDate, endDate)) {
      throw new InputDataException(ConstantsUtil.ERROR_END_DATE_GREATER_START_DATE);
    }
  }
}
