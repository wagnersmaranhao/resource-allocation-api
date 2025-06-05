package com.rm.j2crm.domain.service;

import com.rm.j2crm.domain.dto.ResourceDto;
import com.rm.j2crm.domain.pageable.Filter;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    Page<ResourceDto> list(Optional<Filter> filter, Optional<Integer> page, Optional<Integer> size, Optional<String> sort);
    ResourceDto getById(String id, String fields);
    ResourceDto create(ResourceDto project);
    ResourceDto update(String id, ResourceDto project);
    void remove(String id);
    void upload(String id, MultipartFile file);
    Resource download(String id);
}
