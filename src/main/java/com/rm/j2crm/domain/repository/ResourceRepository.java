package com.rm.j2crm.domain.repository;

import com.rm.j2crm.domain.entity.ResourceEntity;
import com.rm.j2crm.domain.enums.ResourceAvailabilityEnum;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {

  @Query("select p from ResourceEntity p where "
      + "(:firstName is null or p.firstName = :firstName)"
      + "and (:lastName is null or p.lastName = :lastName)"
      + "and (:birthDate is null or p.birthDate = :birthDate)"
      + "and (:role is null or p.role = :role)"
      + "and (:availability is null or p.availability = :availability)"
      + "and (:cvUri is null or p.cvUri = :cvUri)"
      + "and (:cvLastUpdated is null or p.cvLastUpdated = :cvLastUpdated)"
      + "and (:isDeleted is null or p.isDeleted = :isDeleted)")
  Page<ResourceEntity> find(
      @Param("firstName") String firstName,
      @Param("lastName") String lastName,
      @Param("birthDate") Date birthDate,
      @Param("role") String role,
      @Param("availability") ResourceAvailabilityEnum availability,
      @Param("cvUri") String cvUri,
      @Param("cvLastUpdated") Date cvLastUpdated,
      @Param("isDeleted") Boolean isDeleted,
      Pageable pageable
  );
}
