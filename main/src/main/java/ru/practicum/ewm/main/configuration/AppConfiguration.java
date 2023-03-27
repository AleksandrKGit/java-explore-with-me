package ru.practicum.ewm.main.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.practicum.ewm.stat.client.StatClient;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class AppConfiguration {
    @Bean
    StatClient statClient(@Value("${stat-service-url.url}") String statServiceUrl, RestTemplateBuilder builder) {
        return new StatClient(statServiceUrl, builder);
    }
}