package com.rm.j2crm.domain.mapper;

import com.rm.j2crm.api.exceptionhandler.ErrorResponse;

import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ExceptionResponseMapper {

  private static ExceptionResponseMapper INSTANCE;

  public static synchronized ExceptionResponseMapper getInstance() {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new ExceptionResponseMapper();
    }

    return INSTANCE;
  }

  public static ErrorResponse map(String title, String message, String error) {
    return ErrorResponse.builder().title(title).message(message).error(error).build();
  }
}
