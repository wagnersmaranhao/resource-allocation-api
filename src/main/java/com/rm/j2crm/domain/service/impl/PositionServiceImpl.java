package com.rm.j2crm.domain.service.impl;

import com.rm.j2crm.domain.access.AllocationRepositoryAccess;
import com.rm.j2crm.domain.access.PositionRepositoryAccess;
import com.rm.j2crm.domain.access.ProjectRepositoryAccess;
import com.rm.j2crm.domain.access.ResourceRepositoryAccess;
import com.rm.j2crm.domain.dto.AllocationDto;
import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.PositionDto;
import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.mapper.PositionMapper;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.PositionService;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;
import com.rm.j2crm.domain.util.enums.DateEnumUtil;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
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
    log.info("Resource list");

    Pageable pageable = PageableUtil.build(page, size, sort, ConstantsUtil.TITLE);

    Page<PositionEntity> positionEntityPage =
      (filter.isPresent())
        ? positionRepositoryAccess.search(filter.get(), pageable)
        : positionRepositoryAccess.listAll(pageable);

    Page<PositionDto> projectDtoPage = PositionMapper.getInstance().map(positionEntityPage);
    return projectDtoPage;
  }

  @Override
  public PositionDto getById(String id, String fields) {
    log.info("Positiom get by id({})", id);
    // TODO: Implementar o uso do campo fields (campos a serem incluídos na resposta que são entidades estrangeiras) ...
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
      Date date = new Date();
      positionDto.getAllocations().stream().forEach(allocation -> {
        ResourceEntity resource = resourceRepositoryAccess.findById(allocation.getResourceId());
        ref.positionEntity.addResource(allocation, resource, date);
      });
    }

    ref.positionEntity = positionRepositoryAccess.saveOrUpdate(ref.positionEntity);
    allocationRepositoryAccess.saveAll(ref.positionEntity.getAllocations());

    log.info("Position created with id '{}'", ref.positionEntity.getId());
    return PositionMapper.getInstance().map(ref.positionEntity);
  }

  @Override
  public PositionDto update(String id, PositionDto project) {
    return null;
  }

  @Override
  public void remove(String id) {}

  @Override
  public AllocationDto addResource(String positionId, String resourceId, AllocationDto allocation) {
    return null;
  }

  @Override
  public AllocationDto updateAllocation(String positionId, String resourceId, PeriodDto period) {
    return null;
  }

  @Override
  public void removeResource(String positionId, String resourceId) {}

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
