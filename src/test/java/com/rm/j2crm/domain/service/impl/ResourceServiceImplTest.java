package com.rm.j2crm.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import com.rm.j2crm.domain.access.ResourceRepositoryAccess;
import com.rm.j2crm.domain.dto.ProjectDto;
import com.rm.j2crm.domain.dto.ResourceDto;
import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.enums.ResourceAvailabilityEnum;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.exception.RecordDataException;
import com.rm.j2crm.domain.mapper.ResourceMapper;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.h2.store.fs.FileUtils;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

class ResourceServiceImplTest {

  private static final String fileName = "TestUpload.txt";
  private static final String filePath = "C:\\j2c\\files\\";

  private static Path testDir = Paths.get(filePath);
  private static Path testFile = testDir.resolve(fileName);

  @Mock private ResourceRepositoryAccess repositoryAccess;

  @InjectMocks private ResourceServiceImpl service;

  @BeforeEach
  void setup() {
    initMocks(this);
  }

  @BeforeAll
  static void setupFile() throws IOException {
    Files.createDirectories(testDir);
    Files.write(testFile, "conteúdo de teste".getBytes());
  }

  @AfterAll
  static void cleanup() throws IOException {
//    FileUtils.delete(testDir.toString());
//    Files.deleteIfExists(testFile);
//    Files.deleteIfExists(testDir);
  }

  @Test
  @DisplayName("Deve listar os resources com base nos filtros")
  void list() {
    // Arrange
    Optional<Filter> filter = Optional.ofNullable(Filter.builder().name("Test").build());
    Pageable pageable = buildPageable();
    Page<ResourceEntity> page = new PageImpl<>(Arrays.asList(createResourceEntity(true)), pageable, 1);

    when(repositoryAccess.search(any(), any())).thenReturn(page);

    // Act
    Page<ResourceDto> response = service.list(filter, Optional.of(0), Optional.of(20), Optional.of("asc"));

    // Assert
    assertEquals(1, response.getContent().size());
    assertEquals("Test", response.getContent().get(0).getFirstName());
    verify(repositoryAccess, times(1)).search(any(), any());
  }

  @Test
  @DisplayName("Não deve listar os resources quando o filtro não encontra registro")
  void listCase2() {
    Optional<Filter> filter = Optional.ofNullable(Filter.builder().name("Test").build());
    Pageable pageable = buildPageable();
    Page<ResourceEntity> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

    when(repositoryAccess.search(any(), any())).thenReturn(page);

    Page<ResourceDto> response = service.list(filter, Optional.of(0), Optional.of(20), Optional.of("asc"));

    assertThat(response.getContent().isEmpty()).isTrue();
    verify(repositoryAccess, times(1)).search(any(), any());
  }

  @Test
  @DisplayName("Deve obter o resource com sucesso")
  void getByIdCase1() {
    when(repositoryAccess.findById(any())).thenReturn(createResourceEntity(false));
    service.getById(UUID.randomUUID().toString());
    verify(repositoryAccess, times(1)).findById(any());
  }

  @Test
  @DisplayName("Não deve obter o resource quando ele não existe")
  void getByIdCase2() {
    String id = UUID.randomUUID().toString();
    when(repositoryAccess.findById(id)).thenThrow(new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));

    RecordDataException thrown = Assertions.assertThrows(RecordDataException.class, () -> service.getById(id));
    Assertions.assertEquals(ConstantsUtil.ERROR_NOT_FOUND.formatted(id), thrown.getMessage());
  }

  @Test
  @DisplayName("Deve criar o resource com sucesso quando tudo estiver ok")
  void createCase1() {
    ResourceDto dto = createResourceDto(1);
    ResourceEntity entity = ResourceMapper.getInstance().map(dto);

    when(repositoryAccess.saveOrUpdate(entity)).thenReturn(createResourceEntity(true));

    service.create(dto);

    verify(repositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Deve lançar uma exceção quando os dados de entrada forem inválidos")
  void createCase2() {
    ResourceDto dto = createResourceDto(0);
    dto.setFirstName("");

    InputDataException exception = Assertions.assertThrows(InputDataException.class, () -> service.create(dto));
    Assertions.assertEquals(ConstantsUtil.ERROR_INVALID.formatted(ConstantsUtil.FIRST_NAME), exception.getMessage());
  }

  @Test
  @DisplayName("Deve atualizar o resource com sucesso quando tudo estiver ok")
  void updateCase1() {
    ResourceDto dto = createResourceDto(1);
    ResourceEntity entity = createResourceEntity(true);

    when(repositoryAccess.findById(anyString())).thenReturn(entity);
    when(repositoryAccess.saveOrUpdate(entity)).thenReturn(entity);

    service.update(anyString(), dto);
    verify(repositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Não deve atualizar o projeto quando quando ele não existe")
  void updateCase2() {
    ResourceDto dto = createResourceDto(1);
    String id = UUID.randomUUID().toString();

    when(repositoryAccess.findById(id)).thenThrow(new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));

    RecordDataException thrown = Assertions.assertThrows(RecordDataException.class, () -> service.update(id, dto));
    Assertions.assertEquals(ConstantsUtil.ERROR_NOT_FOUND.formatted(id), thrown.getMessage());
  }

  @Test
  @DisplayName("Deve remover o resource quando quando ele existir")
  void removeCase1() {
    ResourceEntity entity = createResourceEntity(true);
    when(repositoryAccess.findById(anyString())).thenReturn(entity);
    when(repositoryAccess.saveOrUpdate(any())).thenReturn(entity);

    service.remove(anyString());

    assertEquals(true, entity.getIsDeleted());
    verify(repositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Não deve remover o resource quando ele não existir")
  void removeCase2() {
    String id = UUID.randomUUID().toString();
    when(repositoryAccess.findById(any())).thenThrow(new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));

    RecordDataException thrown = Assertions.assertThrows(RecordDataException.class, () -> service.remove(id));
    Assertions.assertEquals(ConstantsUtil.ERROR_NOT_FOUND.formatted(id), thrown.getMessage());
  }

  @Test
  @DisplayName("Deve salvar o arquivo e atualizar resource com sucesso quando tudo estiver ok")
  void uploadCase1() {
    MultipartFile mFile = createFile(fileName);
    ResourceEntity mockEntity = createResourceEntity(true);

    when(repositoryAccess.findById(anyString())).thenReturn(mockEntity);
    when(repositoryAccess.saveOrUpdate(any())).thenReturn(mockEntity);

    ReflectionTestUtils.setField(service, "filePath", filePath);
    service.upload(anyString(), mFile);

    assertEquals(mFile.getOriginalFilename(), fileName);
    verify(repositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Não deve salvar o arquivo nem atualizar resource se o arquivo estiver com erro")
  void uploadCase2()  {
    MultipartFile mFile = mock(MultipartFile.class);
    Exception thrown = Assertions.assertThrows(Exception.class, () -> service.upload(anyString(), mFile));
    Assertions.assertEquals(ConstantsUtil.ERROR_UNABLE_UPLOAD, thrown.getMessage());
  }

  @Test
  @DisplayName("Deve obter o arquivo com sucesso quando tudo estiver ok")
  void download() throws Exception {
    ResourceEntity entity = createResourceEntity(true);
    when(repositoryAccess.findById(anyString())).thenReturn(entity);
    service.download(anyString());
    verify(repositoryAccess, times(1)).findById(anyString());
  }

  private MultipartFile createFile(String name) {
    String strName = Strings.isEmpty(name) ? this.fileName : name;

    return new MockMultipartFile(
      strName, strName, "text/plain",
      "This is a dummy file content".getBytes(StandardCharsets.UTF_8)
    );
  }

  private Pageable buildPageable() {
    return PageableUtil.build(
      Optional.of(0),
      Optional.of(20),
      "asc".describeConstable(),
      ConstantsUtil.FIRST_NAME);
  }

  private ResourceDto createResourceDto(int addDays) {
    Date date = new Date();

    return ResourceDto.builder()
      .firstName("Test")
      .lastName("Resource")
      .birthDate(FunctionsUtil.getDate(date, addDays))
      .role("DEV")
      .availability(ResourceAvailabilityEnum.AVAILABLE)
      .cvUri(testFile.toString())
      .cvLastUpdated(FunctionsUtil.dateToString(date))
      .build();
  }

  private ResourceEntity createResourceEntity(boolean withId) {
    String name = "test";
    Date date = new Date();

    ResourceEntity entity = ResourceEntity.builder()
      .id(UUID.randomUUID().toString())
      .firstName("Test")
      .lastName("Resource")
      .birthDate(FunctionsUtil.addDays(date, -10))
      .role("DEV")
      .availability(ResourceAvailabilityEnum.AVAILABLE)
      .cvUri(testFile.toString())
      .cvLastUpdated(date)
      .createBy(name)
      .createOn(date)
      .modifiedBy(name)
      .modifiedOn(date)
      .build();

    if (withId) {
      entity.setId(UUID.randomUUID().toString());
    }

    return entity;
  }
}
