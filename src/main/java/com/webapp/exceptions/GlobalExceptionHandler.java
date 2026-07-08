package com.webapp.exceptions;

import com.webapp.models.dtos.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(InvalidEntityException.class)
  public ResponseEntity<ResponseDTO> invalidEntityException(InvalidEntityException e) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setMessage(e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
  }
}
