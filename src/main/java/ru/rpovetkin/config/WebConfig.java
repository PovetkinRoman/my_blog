package ru.rpovetkin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Можно добавить простые маппинги для статических страниц, если нужно
    }

    @Bean
    public MultipartResolver multipartResolver() {
        // Бин для обработки multipart-запросов (загрузка файлов)
        return new StandardServletMultipartResolver();
    }
}
