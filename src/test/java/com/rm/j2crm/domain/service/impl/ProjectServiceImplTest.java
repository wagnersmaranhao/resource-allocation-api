package com.rm.j2crm.domain.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import com.rm.j2crm.domain.access.ProjectRepositoryAccess;
import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.ProjectDto;
import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.exception.RecordDataException;
import com.rm.j2crm.domain.mapper.ProjectMapper;
import com.rm.j2crm.domain.pageable.Filter;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;
import java.util.*;

import com.rm.j2crm.domain.util.enums.DateEnumUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class ProjectServiceImplTest {

  @Mock private ProjectRepositoryAccess projectRepositoryAccess;

  @InjectMocks private ProjectServiceImpl projectService;

  @BeforeEach
  void setup() {
    initMocks(this);
  }

  @Test
  @DisplayName("Deve listar os projetos com base nos filtros")
  void listCase1() {
    //Arrange
    Optional<Filter> filter = Optional.ofNullable(Filter.builder().name("Test").build());
    Pageable pageable = PageableUtil.build(Optional.of(0), Optional.of(20), "asc".describeConstable(), ConstantsUtil.NAME);
    Page<ProjectEntity> page = new PageImpl<>(Arrays.asList(createProjectEntity(true)), pageable, 1);

    when(projectRepositoryAccess.search(any(), any())).thenReturn(page);

    //Act
    Page<ProjectDto> response = projectService.list(filter, Optional.of(0), Optional.of(20), Optional.of("asc"));

    //Assert
    assertEquals(1, response.getContent().size());
    assertEquals("Test", response.getContent().get(0).getName());
    verify(projectRepositoryAccess, times(1)).search(any(), any());
  }

  @Test
  @DisplayName("Não deve listar os projetos quando o filtro não encontra registro")
  void listCase2() {
    Optional<Filter> filter = Optional.ofNullable(Filter.builder().name("Test").build());
    Pageable pageable = PageableUtil.build(Optional.of(0), Optional.of(20), "asc".describeConstable(), ConstantsUtil.NAME);
    Page<ProjectEntity> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

    when(projectRepositoryAccess.search(any(), any())).thenReturn(page);

    Page<ProjectDto> response = projectService.list(filter, Optional.of(0), Optional.of(20), Optional.of("asc"));

    assertThat(response.getContent().isEmpty()).isTrue();
    verify(projectRepositoryAccess, times(1)).search(any(), any());
  }

  @Test
  @DisplayName("Deve obter o projeto com sucesso")
  void getByIdCase1() {
    when(projectRepositoryAccess.findById(any())).thenReturn(createProjectEntity(false));
    projectService.getById(UUID.randomUUID().toString());
    verify(projectRepositoryAccess, times(1)).findById(any());
  }

  @Test
  @DisplayName("Não deve obter o projeto quando ele não existe")
  void getByIdCase2() {
    String id = UUID.randomUUID().toString();
    when(projectRepositoryAccess.findById(id)).thenThrow(new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));

    RecordDataException thrown = Assertions.assertThrows(RecordDataException.class, () -> projectService.getById(id));

    Assertions.assertEquals(ConstantsUtil.ERROR_NOT_FOUND.formatted(id), thrown.getMessage());
  }

  @Test
  @DisplayName("Deve criar o projeto com sucesso quando tudo estiver ok")
  void createCase1() {
    ProjectDto dto = createProjectDto(1);
    ProjectEntity entity = ProjectMapper.getInstance().map(dto);

    when(projectRepositoryAccess.saveOrUpdate(entity)).thenReturn(createProjectEntity(true));

    projectService.create(dto);

    verify(projectRepositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Deve lançar uma exceção quando os dados de entrada forem inválidos")
  void createCase2() {
    ProjectDto dto = createProjectDto(0);
    InputDataException exception = Assertions.assertThrows(InputDataException.class, () -> projectService.create(dto));
    Assertions.assertEquals(ConstantsUtil.ERROR_END_DATE_GREATER_START_DATE, exception.getMessage());
  }

  @Test
  @DisplayName("Deve atualizar o projeto com sucesso quando tudo estiver ok")
  void updateCase1() {
    ProjectDto dto = createProjectDto(1);
    ProjectEntity entity = createProjectEntity(true);

    when(projectRepositoryAccess.findById(anyString())).thenReturn(entity);
    when(projectRepositoryAccess.saveOrUpdate(entity)).thenReturn(entity);

    projectService.update(anyString(), dto);
    verify(projectRepositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Não deve atualizar o projeto quando quando ele não existe")
  void updateCase2() {
    ProjectDto dto = createProjectDto(1);
    String id = UUID.randomUUID().toString();

    when(projectRepositoryAccess.findById(id)).thenThrow(new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));

    RecordDataException thrown = Assertions.assertThrows(RecordDataException.class, () -> projectService.update(id, dto));
    Assertions.assertEquals(ConstantsUtil.ERROR_NOT_FOUND.formatted(id), thrown.getMessage());
  }

  @Test
  @DisplayName("Deve atualizar o periodo do projeto com sucesso quando tudo estiver ok")
  void updatePatchCase1() {
    PeriodDto period = PeriodDto.builder()
      .startDate(FunctionsUtil.dateToString(new Date()))
      .endDate(FunctionsUtil.getDate(new Date(), 1))
      .build();

    String id = UUID.randomUUID().toString();
    ProjectEntity entity = createProjectEntity(true);

    when(projectRepositoryAccess.findById(id)).thenReturn(entity);
    when(projectRepositoryAccess.saveOrUpdate(entity)).thenReturn(entity);

    ProjectDto result = projectService.updatePatch(id, period);
    assertNotNull(result);
    verify(projectRepositoryAccess, times(1)).saveOrUpdate(entity);
  }

  @Test
  @DisplayName("Não deve atualizar o periodo do projeto quando data for inválida")
  void updatePatchCase2() {
    PeriodDto period = PeriodDto.builder()
      .startDate("20/10/2023")
      .endDate(FunctionsUtil.getDate(new Date(), 1))
      .build();

    InputDataException exception = Assertions.assertThrows(
      InputDataException.class,
      () -> projectService.updatePatch(anyString(), period)
    );

    Assertions.assertEquals(ConstantsUtil.ERROR_INVALID.formatted(ConstantsUtil.START_DATE), exception.getMessage());
  }

  @Test
  @DisplayName("Deve remover o projeto quando quando ele existir")
  void removeCase1() {
    ProjectEntity entity = createProjectEntity(true);
    when(projectRepositoryAccess.findById(anyString())).thenReturn(entity);
    when(projectRepositoryAccess.saveOrUpdate(any())).thenReturn(entity);

    projectService.remove(anyString());

    verify(projectRepositoryAccess, times(1)).saveOrUpdate(any());
  }

  @Test
  @DisplayName("Não deve remover o projeto quando ele não existir")
  void removeCase2() {
    String id = UUID.randomUUID().toString();
    when(projectRepositoryAccess.findById(any())).thenThrow(new RecordDataException(ConstantsUtil.ERROR_NOT_FOUND.formatted(id)));

    RecordDataException thrown = Assertions.assertThrows(RecordDataException.class, () -> projectService.remove(id));
    Assertions.assertEquals(ConstantsUtil.ERROR_NOT_FOUND.formatted(id), thrown.getMessage());
  }

  private ProjectDto createProjectDto(int addDays) {
    Date date = new Date();

    return ProjectDto.builder()
      .name("Wagner")
      .description("Uso do teste Unitário")
      .startDate(FunctionsUtil.dateToString(date))
      .endDate(FunctionsUtil.getDate(date, addDays))
      .status(ProjectStatusEnum.PROPOSAL).build();
  }

  private ProjectEntity createProjectEntity(boolean withId) {
    String name = "test";
    Date date = new Date();
    ProjectEntity entity = ProjectEntity.builder()
      .name("Test")
      .description("Unit Test")
      .startDate(date)
      .endDate(FunctionsUtil.addDays(date, 2))
      .createBy(name)
      .createOn(date)
      .modifiedBy(name)
      .modifiedOn(date)
      .status(ProjectStatusEnum.PROPOSAL)
      .positions(new ArrayList<>()).build();

      if (withId) {
        entity.setId(UUID.randomUUID().toString());
      }

    return entity;
  }
}
