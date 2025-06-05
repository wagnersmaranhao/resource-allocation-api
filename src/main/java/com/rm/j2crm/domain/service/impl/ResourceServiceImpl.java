package com.rm.j2crm.domain.service.impl;

import com.rm.j2crm.domain.access.ResourceRepositoryAccess;
import com.rm.j2crm.domain.dto.ResourceDto;
import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.exception.FileException;
import com.rm.j2crm.domain.mapper.ResourceMapper;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.service.ResourceService;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

  private ResourceRepositoryAccess resourceRepositoryAccess;

  @Autowired
  public ResourceServiceImpl(ResourceRepositoryAccess resourceRepositoryAccess) {
    this.resourceRepositoryAccess = resourceRepositoryAccess;
  }

  @Value("${file.path}")
  private String filePath;

  @Override
  public Page<ResourceDto> list(
      Optional<Filter> filter,
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<String> sort) {
    log.info("Resource list");

    Pageable pageable = PageableUtil.build(page, size, sort, ConstantsUtil.FIRST_NAME);

    Page<ResourceEntity> projectEntityPage =
      (filter.isPresent())
        ? resourceRepositoryAccess.search(filter.get(), pageable)
        : resourceRepositoryAccess.listAll(pageable);

    Page<ResourceDto> projectDtoPage = ResourceMapper.getInstance().map(projectEntityPage);
    return projectDtoPage;
  }

  @Override
  public ResourceDto getById(String id, String fields) {
    log.info("Resource get by id({})", id);
    // TODO: Implementar o uso do campo fields (campos a serem incluídos na resposta que são entidades estrangeiras) ...
    return ResourceMapper.getInstance().map(resourceRepositoryAccess.findById(id));
  }

  @Override
  public ResourceDto create(ResourceDto resource) {
    log.info("Resource create, object='{}'", resource);
    itIsValid(resource);
    ResourceEntity entity =
        resourceRepositoryAccess.saveOrUpdate(ResourceMapper.getInstance().map(resource));

    log.info("Resource created with id '{}'", entity.getId());
    return ResourceMapper.getInstance().map(entity);
  }

  @Override
  public ResourceDto update(String id, ResourceDto resourceDto) {
    log.info("Resource update by id = '{}' and object = '{}'", id, resourceDto);
    itIsValid(resourceDto);

    ResourceEntity entity = resourceRepositoryAccess.findById(id);
    ResourceMapper.getInstance().map(entity, resourceDto);

    return ResourceMapper.getInstance().map(resourceRepositoryAccess.saveOrUpdate(entity));
  }

  @Override
  public void remove(String id) {
    log.info("Resource remove by id '{}'", id);
    ResourceEntity entity = resourceRepositoryAccess.findById(id);
    entity.setIsDeleted(true);

    resourceRepositoryAccess.saveOrUpdate(entity);
  }

  @Override
  @Transactional
  public void upload(String id, MultipartFile file) {
    try {
      String oriFileName = file.getOriginalFilename();
      log.info("Resource upload by id = '{}', file name = '{}'", id, oriFileName);

      ResourceEntity entity = resourceRepositoryAccess.findById(id);

      //TODO: Implementar a validação do tipo do documento para '.pdf', 'docx' e '.png' ...

      long timestamp = System.currentTimeMillis()/1000L;
      String path = filePath.concat(oriFileName.replace(".", "_" + timestamp + "."));

      file.transferTo(new File(path));

      ResourceMapper.getInstance().map(entity, path);
      resourceRepositoryAccess.saveOrUpdate(entity);
    }
    catch (IOException e) {
      throw new FileException(ConstantsUtil.ERROR_UNABLE_UPLOAD);
    }
  }

  @Override
  public Resource download(String id) {
    try {
      log.info("Resource download by id = '{}'", id);
      ResourceEntity entity = resourceRepositoryAccess.findById(id);

      //TODO: Implementar o download já como tipo do documento (.pdf, docx, etc...)
      return new UrlResource(Paths.get(entity.getCvUri()).toUri());
    }
    catch (IOException e) {
      throw new FileException(ConstantsUtil.ERROR_UNABLE_DOWNLOAD);
    }
  }

  private void itIsValid(ResourceDto dto) {
    log.info("Check if input object is valid");
    FunctionsUtil.isValid(dto.getFirstName(), ConstantsUtil.FIRST_NAME);
    FunctionsUtil.isValid(dto.getLastName(), ConstantsUtil.LAST_NAME);
    FunctionsUtil.isValidDate(dto.getBirthDate(), ConstantsUtil.BIRTH_DATE);
    FunctionsUtil.isValid(dto.getRole(), ConstantsUtil.ROLE);
    FunctionsUtil.isValid(dto.getCvUri(), ConstantsUtil.CV_URI);
    FunctionsUtil.isValidDate(dto.getCvLastUpdated(), ConstantsUtil.CV_LAST_UPDATE);
  }
}
