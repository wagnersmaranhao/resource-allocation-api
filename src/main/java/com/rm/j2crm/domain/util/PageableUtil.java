package com.rm.j2crm.domain.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Slf4j
public class PageableUtil {
  public static Pageable build(
      Optional<Integer> page, Optional<Integer> size, Optional<String> sort, String sortBy) {

    log.info("Pageable build with page = '{}', size = '{}', sort = '{}', sortBy = '{}'", page, size, sort, sort);
    String strSort = sort.orElse(ConstantsUtil.SORT_DIRECTION_ASC);

    Sort.Direction sortDirection =
        strSort.equals(ConstantsUtil.SORT_DIRECTION_ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

    return PageRequest.of(page.orElse(0), size.orElse(20), sortDirection, sortBy);
  }
}
