package com.rm.j2crm.domain.entity;

import com.rm.j2crm.domain.enums.ResourceAvailabilityEnum;
import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_resource")
public class ResourceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "birth_date")
  private Date birthDate;

  @Column(nullable = false)
  private String role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ResourceAvailabilityEnum availability;

  @Column(name = "cv_uri")
  private String cvUri;

  @Column(name = "cv_last_update")
  private Date cvLastUpdated;

  @OneToMany(mappedBy = "resource")
  private List<AllocationEntity> allocations;

  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  private Boolean isDeleted;

  @Column(name = "create_by")
  private String createBy;

  @Column(name = "create_on", nullable = false)
  private Date createOn;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on", nullable = false)
  private Date modifiedOn;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResourceEntity tag = (ResourceEntity) o;
    return Objects.equals(id, tag.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}