package ru.rpovetkin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.rpovetkin"})
@PropertySource("classpath:application.properties")
public class WebConfiguration {

    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
