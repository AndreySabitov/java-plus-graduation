package ru.practicum.ewm.client.config;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.client.error.CustomErrorDecoder;

@Configuration
public class FeignConfig {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .errorDecoder(new CustomErrorDecoder());
    }
}
