package com.rm.j2crm.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rm.j2crm.domain.entity.PositionEntity;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
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
public class ProjectDto {
    private String id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private ProjectStatusEnum status;

    private List<PositionDto> positions;
}
