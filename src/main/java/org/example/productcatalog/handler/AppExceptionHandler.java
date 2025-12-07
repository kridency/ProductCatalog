package org.example.productcatalog.handler;

import org.example.productcatalog.dto.MessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(value = HandlerMethodValidationException.class)
    public ResponseEntity<MessageDto> handleMethodArgumentNotValid(HandlerMethodValidationException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, request);
    }

    private ResponseEntity<MessageDto> buildResponse(HttpStatus httpStatus, Exception ex, WebRequest webRequest){
        return ResponseEntity.status(httpStatus)
                .body(MessageDto.builder()
                        .message(ex.getMessage())
                        .description(webRequest.getDescription(false))
                        .build());
    }
}
