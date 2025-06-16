package com.rm.j2crm.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import com.rm.j2crm.domain.util.ConstantsUtil;
import com.rm.j2crm.domain.util.FunctionsUtil;
import com.rm.j2crm.domain.util.PageableUtil;
import jakarta.persistence.EntityManager;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProjectRepositoryTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private ProjectRepository repository;

  @Test
  @DisplayName("Deve obter o projeto com sucesso do banco de dados")
  void findByFiltersCase1() {
    crateListProjects(2);
    Pageable pageable = PageableUtil.build(Optional.of(0), Optional.of(20), "asc".describeConstable(), ConstantsUtil.NAME);
    Page<ProjectEntity> result = repository.findByFilters("Test 1", null, null, null, null, null, pageable);

    assertEquals(1, result.getContent().size());
  }

  @Test
  @DisplayName("Não deve obter o projeto do banco de dados quando ele não existe")
  void findByFiltersCase2() {
    Pageable pageable = PageableUtil.build(Optional.of(0), Optional.of(20), "asc".describeConstable(), ConstantsUtil.NAME);
    Page<ProjectEntity> result = repository.findByFilters("Wagner", null, null, null, null, null, pageable);

    assertThat(result.getContent().isEmpty()).isTrue();
  }

  private void crateListProjects(int totalElement) {
    String name = "test.j2c";
    Date date = new Date();

    for (int i = 0; i < totalElement; i++) {
      ProjectEntity entity = ProjectEntity.builder()
        .name("Test %d".formatted(i))
        .description("Teste Unitário %d".formatted(i))
        .startDate(date)
        .endDate(FunctionsUtil.addDays(date, 2))
        .createBy(name)
        .createOn(date)
        .modifiedBy(name)
        .modifiedOn(date)
        .status(ProjectStatusEnum.PROPOSAL)
        .build();

      entityManager.persist(entity);
    }
  }
}
