package com.rm.j2crm.api.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.rm.j2crm.domain.exception.FileException;
import com.rm.j2crm.domain.exception.FilterException;
import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.exception.RecordDataException;
import com.rm.j2crm.domain.mapper.ExceptionResponseMapper;
import com.rm.j2crm.domain.util.ConstantsUtil;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestController
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
  public ResponseEntity<?> handleAllException(Exception ex) {
    String msg = "We were unable to complete your request, please contact your system administrator.";
    return handleResponse(HttpStatus.INTERNAL_SERVER_ERROR, msg, ex.getMessage());
  }

  @ExceptionHandler({InputDataException.class, FilterException.class, FileException.class})
  public ResponseEntity<?> handlerValidationException(RuntimeException ex) {
    String message = (ex instanceof InputDataException)
      ? "Invalid input data" : (ex instanceof FilterException) ? "Invalid filter criteria" : "Download Error";
    return handleResponse(HttpStatus.BAD_REQUEST, message, ex.getMessage());
  }

  @ExceptionHandler(RecordDataException.class)
  public ResponseEntity<?> handlerRecordDataException(RuntimeException ex) {
    return handleResponse(HttpStatus.NOT_FOUND, "Project not found", ex.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
          HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Throwable rootCause = getRootCause(ex);

    String error, path;

    if (rootCause instanceof InvalidFormatException) {
      InvalidFormatException iex = (InvalidFormatException)rootCause;

      path = iex.getPath().stream().map(ref -> ref.getFieldName()).collect(Collectors.joining("."));
      error = ConstantsUtil.ERROR_PROPERTY_TYPE.formatted(path, iex.getValue(), iex.getTargetType().getSimpleName());
    }
    else if (rootCause instanceof PropertyBindingException) {
      PropertyBindingException pex = (PropertyBindingException)rootCause;

      path = pex.getPath().stream().map(ref -> ref.getFieldName()).collect(Collectors.joining("."));
      error = ConstantsUtil.ERROR_PROPERTY_NOT_FOUND.formatted(path);
    }
    else {
      error = ConstantsUtil.ERROR_BODY_INVALID;
    }

    return (ResponseEntity<Object>) handleResponse(HttpStatus.valueOf(status.value()), "Incomprehensible Message", error);
  }

  private ResponseEntity<?> handleResponse(HttpStatus status, String message, String error) {
    log.error("{}, error: {}", message, error);
    ErrorResponse response = ExceptionResponseMapper.getInstance().map(status.getReasonPhrase(), message, error);
    return new ResponseEntity<>(response, status);
  }

  private Throwable getRootCause(Throwable e) {
    Throwable cause = null;
    Throwable result = e;

    while(null != (cause = result.getCause())  && (result != cause) ) {
      result = cause;
    }
    return result;
  }
}
