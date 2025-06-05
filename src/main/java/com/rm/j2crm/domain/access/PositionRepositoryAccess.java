package com.rm.j2crm.domain.access;

import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.enums.PositionStatusEnum;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.exception.RecordDataException;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.repository.PositionRepository;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PositionRepositoryAccess {

  @Value("${user.name}")
  private String userName;

  private PositionRepository repository;

  private ProjectRepositoryAccess projectRepositoryAccess;

  @Autowired
  public PositionRepositoryAccess(PositionRepository repository, ProjectRepositoryAccess projectRepositoryAccess) {
    this.repository = repository;
    this.projectRepositoryAccess = projectRepositoryAccess;
  }

  public PositionEntity saveOrUpdate(PositionEntity entity) {
    log.info("Position repository access save or update");
    Date date = new Date();

    if (Objects.isNull(entity.getId())) {
      entity.setIsDeleted(false);
      entity.setCreateBy(userName);
      entity.setCreateOn(date);
    }

    entity.setModifiedBy(userName);
    entity.setModifiedOn(date);

    return repository.saveAndFlush(entity);
  }

  public Page<PositionEntity> listAll(Pageable pageable) {
    log.info("Position repository list all");
    return repository.findAll(pageable);
  }

  @Transactional
  public Page<PositionEntity> search(Filter filter, Pageable pageable) {
    log.info("Position repository access search with filer = '{}'", filter);
    boolean hasStarDate = Strings.isNotEmpty(filter.getStartDate());
    boolean hasEndDate = Strings.isNotEmpty(filter.getEndDate());

    if (hasStarDate && !FunctionsUtil.isMatch(filter.getStartDate(), ConstantsUtil.DATE_REGEX)) {
      throw new InputDataException(ConstantsUtil.ERROR_INVALID.formatted(ConstantsUtil.START_DATE));
    }
    if (hasEndDate && !FunctionsUtil.isMatch(filter.getEndDate(), ConstantsUtil.DATE_REGEX)) {
      throw new InputDataException(ConstantsUtil.ERROR_INVALID.formatted(ConstantsUtil.END_DATE));
    }

    ProjectEntity project = null;

    if (Strings.isNotEmpty(filter.getProjectId())) {
      project = projectRepositoryAccess.findById(filter.getProjectId());
    }

    return repository.find(
      project,
      filter.getTitle(),
      filter.getDescription(),
      filter.getRole(),
      filter.getNumberOfResources(),
      hasStarDate ? FunctionsUtil.stringToDate(filter.getStartDate()) : null,
      hasEndDate ? FunctionsUtil.stringToDate(filter.getEndDate()) : null,
      Strings.isNotEmpty(filter.getStatus()) ? PositionStatusEnum.getStatus(filter.getStatus()) : null,
      FunctionsUtil.strToBool(filter.getIsDeleted()),
      pageable
    );
  }

  public PositionEntity findById(String id) {
    log.info("Position repository access find by id = '{}'", id);
    return repository.findById(id)
      .orElseThrow(() -> new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));
  }
}
