package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.util.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// #SPRING SECURITY# 14
@Slf4j // untuk logger
@RestControllerAdvice // @RestControllerAdvice adalah annotation gabungan dari @ControllerAdvice dan @ResponseBody
// inget kalau kita pakai ExceptionHandler aja itu harus didalam setiap @RestController dan hanya berlaku di controller tersebut saja
// nah dengan @ControllerAdvice itu menjadi berlaku untuk semua controller ("apply globally to all controllers", menangani exception secara global)
// @ResponseBody (Annotation that indicates a method return value should be bound to the web response body)
// jadi mampu mengubah response menjadi format JSON
public class ErrorController {
    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException e) {
        log.error("handleResponseStatusException" + e.getMessage());
        HttpStatusCode statusCode = e.getStatusCode();
        return ResponseUtil.buildResponse(HttpStatus.valueOf(statusCode.value()), e.getReason(), null);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("handleDataIntegrityViolationException" + e.getMessage());
        String message = "";
        HttpStatus status = HttpStatus.CONFLICT;

        if (e.getCause() != null) {
            String causeMessage = e.getCause().getMessage().toLowerCase();

            if(causeMessage.contains("duplicate key value")) {
                message = "Data already exist.";
            } else if (causeMessage.contains("cannot be null")) {
                message = "Data cannot be null.";
                status = HttpStatus.BAD_REQUEST;
            } else if (causeMessage.contains("foreign key constraint")) {
                message = "Data cannot be deleted because it is used by other data.";
                status = HttpStatus.BAD_REQUEST;
            } else if (causeMessage.contains("duplicate entry")) {
                message = "Data already exist.";
            } else {
                message = "Unexpected error.";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return ResponseUtil.buildResponse(status, message, null);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("handleConstraintViolationException" + e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValid" + e.getMessage());
        List<String> errors = e.getBindingResult() // untuk mendapatkan semua informasi tentang error yang terjadi saat validasi
                .getFieldErrors() // List of FieldErorr
                .stream()// List maka bisa kita stream
                .map(FieldError::getDefaultMessage) // lalu kita map ambil error message dengan getDefaultMessage
                .toList();

        String errorMessage = String.join(", ", errors);
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, errorMessage, null);
    }

}
