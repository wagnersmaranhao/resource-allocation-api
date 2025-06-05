package com.rm.j2crm.domain.entity;

import com.rm.j2crm.domain.enums.AllocationStatusEnum;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_allocation")
public class AllocationEntity {

  @EmbeddedId
  private AllocationEntityId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("positionId")
  private PositionEntity position;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("resourceId")
  private ResourceEntity resource;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AllocationStatusEnum status;

  @Column(name = "start_date")
  private Date startDate;

  @Column(name = "end_date")
  private Date endDate;

  @Column(name = "is_deleted")
  private Boolean isDeleted;

  @Column(name = "create_by")
  private String createBy;

  @Column(name = "create_on", nullable = false)
  private Date createOn;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on", nullable = false)
  private Date modifiedOn;

  public AllocationEntity(PositionEntity position, ResourceEntity resource) {
    this.position = position;
    this.resource = resource;
    this.id = new AllocationEntityId(position.getId(), resource.getId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass())
      return false;

    AllocationEntity that = (AllocationEntity) o;
    return Objects.equals(position, that.position) &&
            Objects.equals(resource, that.resource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, resource);
  }
}
