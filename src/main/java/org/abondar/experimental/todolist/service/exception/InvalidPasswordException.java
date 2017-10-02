package org.abondar.experimental.todolist.service.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable ex) {
        super(message, ex);
    }

}
