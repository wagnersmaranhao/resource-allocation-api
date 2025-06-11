package com.rm.j2crm.domain.repository;

import com.rm.j2crm.domain.entity.AllocationEntity;
import com.rm.j2crm.domain.entity.AllocationEntityId;
import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllocationRepository extends JpaRepository<AllocationEntity, AllocationEntityId> {

//    Optional<AllocationEntity> findById(@Param("position") PositionEntity position, @Param("resource") ResourceEntity resource);

//    @Override
//    @Query("select a from AllocationEntity a where a.position = :position and a.resource = :resource")
//    Optional<AllocationEntity> findById(AllocationEntityId allocationEntityId);
}
