package hello.itemservice.config;

import hello.itemservice.repository.jpa.JpqlJoinPractice;
import hello.itemservice.repository.jpa.JpqlJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JpqlJoinPracticeConfig {
    private final JpqlJoinPractice jpqlJoinPractice;
    @Bean
    public JpqlJoinRepository jpqlJoinRepository(){
        return new JpqlJoinRepository(jpqlJoinPractice);
    }
}
