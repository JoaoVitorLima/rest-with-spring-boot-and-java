package br.com.erudio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageExceptionException extends RuntimeException {
    public FileStorageExceptionException(String message) {
        super(message);
    }

    public FileStorageExceptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
