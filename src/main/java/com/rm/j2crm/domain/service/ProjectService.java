package com.rm.j2crm.domain.service;

import com.rm.j2crm.domain.dto.PeriodDto;
import com.rm.j2crm.domain.dto.ProjectDto;
import com.rm.j2crm.domain.pageable.Filter;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProjectService {
    Page<ProjectDto> list(Optional<Filter> filter, Optional<Integer> page, Optional<Integer> size, Optional<String> sort);
    ProjectDto getById(String id, String fields);
    ProjectDto create(ProjectDto project);
    ProjectDto update(String id, ProjectDto project);
    ProjectDto updatePatch(String id, PeriodDto period);
    void remove(String id);
}
