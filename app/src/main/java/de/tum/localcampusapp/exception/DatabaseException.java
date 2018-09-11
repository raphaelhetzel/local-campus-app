package de.tum.localcampusapp.exception;

public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException() {
        super();
    }
}
