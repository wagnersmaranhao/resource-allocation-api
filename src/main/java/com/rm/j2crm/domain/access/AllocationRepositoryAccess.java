package com.rm.j2crm.domain.access;

import com.rm.j2crm.domain.entity.AllocationEntity;
import com.rm.j2crm.domain.repository.AllocationRepository;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AllocationRepositoryAccess {
  @Value("${user.name}")
  private String userName;

  private AllocationRepository repository;

  public AllocationRepositoryAccess(AllocationRepository repository) {
    this.repository = repository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void save(AllocationEntity entity, Date date) {
    log.info("Allocation repository access save or update");

    if (Objects.isNull(date)) {
      date = new Date();
    }

    if ( Strings.isEmpty(entity.getCreateBy())) {
      entity.setIsDeleted(false);
      entity.setCreateBy(userName);
      entity.setCreateOn(date);
    }

    entity.setModifiedBy(userName);
    entity.setModifiedOn(date);

    repository.save(entity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveAll(List<AllocationEntity> list) {
    Date date = new Date();
    list.stream().forEach(p -> save(p, date));
  }
}