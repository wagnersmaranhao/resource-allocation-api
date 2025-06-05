package com.rm.j2crm.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rm.j2crm.domain.enums.PositionStatusEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PositionDto {
    private String id;
    private String projectId;
    private String title;
    private String description;
    private String role;
    private Integer numberOfResources;
    private String startDate;
    private String endDate;
    private PositionStatusEnum status;

    private List<AllocationDto> allocations;
}
