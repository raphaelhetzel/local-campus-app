package de.tum.localcampusapp.exception;

public class MissingRelatedDataException extends Exception {
    public MissingRelatedDataException(String message) {
        super(message);
    }

    public MissingRelatedDataException() {
        super();
    }
}
