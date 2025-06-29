package com.rm.j2crm.domain.entity;

import com.rm.j2crm.domain.dto.AllocationDto;
import com.rm.j2crm.domain.enums.PositionStatusEnum;
import com.rm.j2crm.domain.mapper.AllocationMapper;
import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinFormula;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_position")
public class PositionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "project_id", nullable = false)
  private ProjectEntity project;

  @Column(nullable = false)
  private String title;

  private String description;
  private String role;

  @Column(name = "number_of_resources")
  private Integer numberOfResources;

  @Column(name = "start_date", nullable = false)
  private Date startDate;

  @Column(name = "end_date", nullable = false)
  private Date endDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PositionStatusEnum status;

  @OneToMany(mappedBy = "position")
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

  public void addResource(AllocationDto dto, ResourceEntity resource) {
    AllocationEntity allocation = AllocationMapper.getInstance().map(dto, this, resource);
    this.allocations.add(allocation);
    resource.getAllocations().add(allocation);
  }

  public void removeResource(ResourceEntity resource) {
    allocations.stream()
      .filter(p -> p.getResource().getId().equals(resource.getId()))
      .forEach(v -> v.setIsDeleted(true));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PositionEntity)) return false;
    return id != null && id.equals(((PositionEntity) o).getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
