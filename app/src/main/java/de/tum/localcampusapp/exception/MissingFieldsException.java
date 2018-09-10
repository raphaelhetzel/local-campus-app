package de.tum.localcampusapp.exception;

public class MissingFieldsException extends Exception {

    public MissingFieldsException(String message) {
        super(message);
    }

    public MissingFieldsException() {
        super();
    }
}
