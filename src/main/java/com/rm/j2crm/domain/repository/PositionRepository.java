package com.rm.j2crm.domain.repository;

import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.entity.ProjectEntity;
import com.rm.j2crm.domain.enums.PositionStatusEnum;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, String> {

    @Query("select p from PositionEntity p " +
           "where (:project is null or p.project = :project)" +
           "and (:title is null or p.title = :title)" +
           "and (:description is null or p.description = :description)" +
           "and (:role is null or p.role = :role)" +
           "and (:numberOfResources is null or p.numberOfResources = :numberOfResources)" +
           "and (:startDate is null or p.startDate = :startDate)" +
           "and (:endDate is null or p.endDate = :endDate)" +
           "and (:status is null or p.status = :status)" +
           "and (:isDeleted is null or p.isDeleted = :isDeleted)")
  Page<PositionEntity> find(
    @Param("project") ProjectEntity project,
    @Param("title") String title,
    @Param("description") String description,
    @Param("role") String role,
    @Param("numberOfResources") Integer numberOfResources,
    @Param("startDate") Date startDate,
    @Param("endDate") Date endDate,
    @Param("status") PositionStatusEnum status,
    @Param("isDeleted") Boolean isDeleted,
    Pageable pageable);

    Optional<PositionEntity> findByIdAndIsDeleted(String s, Boolean isDeleted);
}
