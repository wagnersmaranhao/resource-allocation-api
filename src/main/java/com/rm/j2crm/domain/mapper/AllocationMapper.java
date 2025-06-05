package com.rm.j2crm.domain.mapper;

import com.rm.j2crm.domain.dto.AllocationDto;
import com.rm.j2crm.domain.dto.PositionDto;
import com.rm.j2crm.domain.entity.AllocationEntity;
import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.util.FunctionsUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AllocationMapper {

  @Value("${user.name}")
  private static String userName;

  private static AllocationMapper INSTANCE;

  public static synchronized AllocationMapper getInstance() {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new AllocationMapper();
    }

    return INSTANCE;
  }

  public static AllocationDto map(AllocationEntity entity) {
      log.info("Allocation Mapper Entity to DTO");
    return AllocationDto.builder()
      .positionId(entity.getPosition().getId())
      .resourceId(entity.getResource().getId())
      .status(entity.getStatus())
      .startDate(FunctionsUtil.dateToString(entity.getStartDate()))
      .endDate(FunctionsUtil.dateToString(entity.getEndDate()))
      .build();
  }

  public static AllocationEntity map(
    AllocationDto dto, PositionEntity position, ResourceEntity resource, Date date) {
      log.info("Allocation Mapper Dto to Entity");
      AllocationEntity entity = new AllocationEntity(position, resource);
      entity.setStatus(dto.getStatus());
      entity.setStartDate(FunctionsUtil.stringToDate(dto.getStartDate()));
      entity.setEndDate(FunctionsUtil.stringToDate(dto.getEndDate()));
      entity.setCreateBy(userName);
      entity.setCreateOn(date);
      entity.setModifiedBy(userName);
      entity.setModifiedOn(date);

      return entity;
  }

  public static void map(AllocationEntity targetEntity, AllocationDto anotherDto) {
    log.info("Allocation Mapper DTO to Entity");
    targetEntity.setStatus(anotherDto.getStatus());
    targetEntity.setStartDate(FunctionsUtil.stringToDate(anotherDto.getStartDate()));
    targetEntity.setEndDate(FunctionsUtil.stringToDate(anotherDto.getEndDate()));
  }

  public static List<AllocationDto> map(List<AllocationEntity> list) {
    return (Objects.isNull(list) || list.isEmpty())
      ? Arrays.asList()
      : list.stream().map(e -> map(e)).collect(Collectors.toList());
  }
}
