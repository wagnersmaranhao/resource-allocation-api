package com.rm.j2crm.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rm.j2crm.domain.enums.ResourceAvailabilityEnum;
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
public class ResourceDto {
    private String id;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String role;
    private ResourceAvailabilityEnum availability;
    private String cvUri;
    private String cvLastUpdated;

    private List<AllocationDto> allocations;
}
