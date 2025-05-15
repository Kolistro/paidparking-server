package ru.omgu.paidparking_server.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.omgu.paidparking_server.dto.response.CommonResponse;
import ru.omgu.paidparking_server.exception.BuildingAlreadyExistsException;
import ru.omgu.paidparking_server.exception.BuildingNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class BuildingExceptionHandler {
    @ExceptionHandler(BuildingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CommonResponse<Object>> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(BuildingAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<CommonResponse<Object>> handleAlreadyExistsException(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new CommonResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }
}
