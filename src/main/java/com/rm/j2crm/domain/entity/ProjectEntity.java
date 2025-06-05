package com.rm.j2crm.domain.entity;

import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_project")
public class ProjectEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(name = "start_date")
  private Date startDate;

  @Column(name = "end_date")
  private Date endDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProjectStatusEnum status;

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

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<PositionEntity> positions;
}
