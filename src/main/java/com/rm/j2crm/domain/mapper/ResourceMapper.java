package com.rm.j2crm.domain.mapper;

import com.rm.j2crm.domain.dto.ResourceDto;
import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.util.FunctionsUtil;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourceMapper {
  private static ResourceMapper INSTANCE;

  public static synchronized ResourceMapper getInstance() {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new ResourceMapper();
    }

    return INSTANCE;
  }

  public static ResourceDto map(ResourceEntity entity) {
    log.info("Resource Mapper entity to DTO");
    return ResourceDto.builder()
        .id(entity.getId())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .birthDate(FunctionsUtil.dateToString(entity.getBirthDate()))
        .role(entity.getRole())
        .availability(entity.getAvailability())
        .cvUri(entity.getCvUri())
        .cvLastUpdated(FunctionsUtil.dateToString(entity.getCvLastUpdated()))
        .build();
  }

  public static ResourceEntity map(ResourceDto dto) {
    log.info("Resource Mapper DTO to Entity");
    return ResourceEntity.builder()
        .id(dto.getId())
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .birthDate(FunctionsUtil.stringToDate(dto.getBirthDate()))
        .role(dto.getRole())
        .availability(dto.getAvailability())
        .cvUri(dto.getCvUri())
        .cvLastUpdated(FunctionsUtil.stringToDate(dto.getCvLastUpdated()))
        .build();
  }

  public static Page<ResourceDto> map(Page<ResourceEntity> projectsPage) {
    log.info("Resource Mapper list entity to list DTO");
    return projectsPage.map(entity -> map(entity));
  }

  public static void map(ResourceEntity targetEntity, ResourceDto dto) {
    log.info("Project Mapper target entity to another entity");
    targetEntity.setFirstName(dto.getFirstName());
    targetEntity.setLastName(dto.getLastName());
    targetEntity.setBirthDate(FunctionsUtil.stringToDate(dto.getBirthDate()));
    targetEntity.setRole(dto.getRole());
    targetEntity.setAvailability(dto.getAvailability());
    targetEntity.setCvUri(dto.getCvUri());
    targetEntity.setCvLastUpdated(FunctionsUtil.stringToDate(dto.getCvLastUpdated()));
  }

  public static void map(ResourceEntity entity, String filePath) {
    log.info("Resource Mapper file to Entity");
    entity.setCvUri(filePath);
    entity.setCvLastUpdated(new Date());
  }
}
