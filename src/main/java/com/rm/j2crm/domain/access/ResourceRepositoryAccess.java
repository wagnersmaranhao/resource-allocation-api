package com.rm.j2crm.domain.access;

import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.enums.ResourceAvailabilityEnum;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.exception.RecordDataException;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.repository.ResourceRepository;
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

@Slf4j
@Component
public class ResourceRepositoryAccess {

  @Value("${user.name}")
  private String userName;

  private ResourceRepository repository;

  @Autowired
  public ResourceRepositoryAccess(ResourceRepository resourceRepository) {
    this.repository = resourceRepository;
  }

  public Page<ResourceEntity> listAll(Pageable pageable) {
    log.info("Resource repository list all");
    return repository.findAll(pageable);
  }

  public ResourceEntity saveOrUpdate(ResourceEntity entity) {
    log.info("Resource repository access save or update");
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

  public Page<ResourceEntity> search(Filter filter, Pageable pageable) {
    log.info("Resource repository access search with filer = '{}'", filter);
    boolean hasBirthDate = Strings.isNotEmpty(filter.getBirthDate());
    boolean hasCvLastUpdate = Strings.isNotEmpty(filter.getCvLastUpdated());

    if (hasBirthDate && !FunctionsUtil.isMatch(filter.getBirthDate(), ConstantsUtil.DATE_REGEX)) {
      throw new InputDataException(ConstantsUtil.ERROR_INVALID.formatted(ConstantsUtil.START_DATE));
    }
    if (hasCvLastUpdate
        && !FunctionsUtil.isMatch(filter.getCvLastUpdated(), ConstantsUtil.DATE_REGEX)) {
      throw new InputDataException(ConstantsUtil.ERROR_INVALID.formatted(ConstantsUtil.END_DATE));
    }

    //TODO: Implementar o find também por posisiton ....
    return repository.findByFilters(
      filter.getFirstName(),
      filter.getLastName(),
      hasBirthDate ? FunctionsUtil.stringToDate(filter.getBirthDate()) : null,
      filter.getRole(),
      Strings.isNotEmpty(filter.getAvailability()) ? ResourceAvailabilityEnum.getAvailability(filter.getAvailability()) : null,
      filter.getCvUri(),
      hasCvLastUpdate ? FunctionsUtil.stringToDate(filter.getCvLastUpdated()) : null,
      FunctionsUtil.strToBool(filter.getIsDeleted()),
      pageable
    );
  }

  public ResourceEntity findById(String id) {
    log.info("Resource repository access find by id = '{}'", id);
    return repository.findById(id)
      .orElseThrow(() -> new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));
  }
}
