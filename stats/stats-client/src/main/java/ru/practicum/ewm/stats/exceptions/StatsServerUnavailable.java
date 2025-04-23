package ru.practicum.ewm.stats.exceptions;

public class StatsServerUnavailable extends RuntimeException {
    public StatsServerUnavailable(String message, Exception exception) {
        super(message);
    }
}
