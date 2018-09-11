package de.tum.localcampusapp.exception;

public class MissingTopicException extends Exception {
    public MissingTopicException(String message) {
        super(message);
    }

    public MissingTopicException() {
        super();
    }
}
