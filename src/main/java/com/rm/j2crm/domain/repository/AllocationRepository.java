package com.rm.j2crm.domain.repository;

import com.rm.j2crm.domain.entity.AllocationEntity;
import com.rm.j2crm.domain.entity.AllocationEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllocationRepository extends JpaRepository<AllocationEntity, AllocationEntityId> {}
