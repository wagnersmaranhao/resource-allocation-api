package com.rm.j2crm.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
  public class AllocationEntityId implements Serializable {

    @Column(name = "position_id")
    private String positionId;

    @Column(name = "resource_id")
    private String resourceId;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AllocationEntityId)) return false;
      AllocationEntityId that = (AllocationEntityId) o;
      return Objects.equals(getPositionId(), that.getPositionId()) &&
              Objects.equals(getResourceId(), that.getResourceId());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getPositionId(), getResourceId());
    }
  }