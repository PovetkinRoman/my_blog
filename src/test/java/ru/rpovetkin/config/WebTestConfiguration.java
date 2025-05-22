package ru.rpovetkin.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.rpovetkin.service.FileStorageService;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.rpovetkin"})
public class WebTestConfiguration {

    @Bean
    @Primary
    public FileStorageService mockFileStorageService() {
        return Mockito.mock(FileStorageService.class);
    }
}
