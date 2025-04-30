package ru.practicum.ewm.exception;

public class NotFoundRecordInBDException extends RuntimeException {
    public NotFoundRecordInBDException(String message) {
        super(message);
    }
}
