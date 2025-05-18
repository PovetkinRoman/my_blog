//package ru.rpovetkin.config;
//
//import org.mockito.Mockito;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import ru.rpovetkin.repository.PostRepository;
//import ru.rpovetkin.service.FileStorageService;
//
//@Configuration
//@ComponentScan("ru.rpovetkin.service")
//public class TestConfig {
//
//    @Bean
//    @Primary
//    public PostRepository mockPostRepository() {
//        return Mockito.mock(PostRepository.class);
//    }
//
//    @Bean
//    @Primary
//    public CommentRepository mockCommentRepository() {
//        return Mockito.mock(CommentRepository.class);
//    }
//
//    @Bean
//    @Primary
//    public FileStorageService mockFileStorageService() {
//        return Mockito.mock(FileStorageService.class);
//    }
//}
