package com.rm.j2crm.domain.pageable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rm.j2crm.domain.enums.PositionStatusEnum;
import com.rm.j2crm.domain.enums.ProjectStatusEnum;
import com.rm.j2crm.domain.enums.ResourceAvailabilityEnum;
import com.rm.j2crm.domain.util.FunctionsUtil;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Filter {

    private String description;
    private String role;
    private String startDate;
    private String endDate;
    private String status;
    private String isDeleted;

    //Project
    private String name;

    //Resource
    private String firstName;
    private String lastName;
    private String birthDate;
    private String availability;
    private String cvUri;
    private String cvLastUpdated;

    //Position
    private String projectId;
    private String title;
    private Integer numberOfResources;
}
