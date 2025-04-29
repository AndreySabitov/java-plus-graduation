package ru.practicum.ewm.client.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ServerUnavailable;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 404) {
            throw new NotFoundException("При выполнении метода %s произошла ошибка Not Found".formatted(s));
        } else if (response.status() == 500) {
            throw new ServerUnavailable("При выполнении метода %s произошла ошибка ServerUnavailable".formatted(s));
        } else {
            return errorDecoder.decode(s, response);
        }
    }
}
