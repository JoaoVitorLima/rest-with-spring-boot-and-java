package br.com.erudio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestlException extends RuntimeException {
    public BadRequestlException() {
        super("Unsupported file extension!");
    }

    public BadRequestlException(String message) {
        super(message);
    }
}
