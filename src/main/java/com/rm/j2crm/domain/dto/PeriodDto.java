package com.rm.j2crm.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rm.j2crm.domain.enums.AllocationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodDto {
    private String startDate;
    private String endDate;
    private AllocationStatusEnum status;
}
