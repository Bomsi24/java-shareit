package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ValidationException.class, MethodArgumentNotValidException.class})
    public ErrorResponse handleValidationException(final ValidationException exception) {
        return new ErrorResponse("Ошибка валидации", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFound(final NotFoundException exception) {
        return new ErrorResponse("Искомый объект не найден", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class,InternalServerException.class})
    public ErrorResponse handleInternalServerErrorException(final InternalServerException exception) {
        return new ErrorResponse("Возникло исключение.", exception.getMessage());
    }
}