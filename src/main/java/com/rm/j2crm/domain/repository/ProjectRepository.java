package com.rm.j2crm.domain.repository;

import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

  @Query("select p from ProjectEntity p where " +
         "(:name is null or p.name = :name)" +
          "and (:description is null or p.description = :description)" +
          "and (:startDate is null or p.startDate = :startDate)" +
          "and (:endDate is null or p.endDate = :endDate)" +
          "and (:status is null or p.status = :status)" +
          "and (:isDeleted is null or p.isDeleted = :isDeleted)")
  Page<ProjectEntity> findByFilters(
    @Param("name") String name,
    @Param("description") String description,
    @Param("startDate") Date startDate,
    @Param("endDate") Date endDate,
    @Param("status") ProjectStatusEnum status,
    @Param("isDeleted") Boolean isDeleted,
    Pageable pageable
  );
}
