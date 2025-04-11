package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class JpqlJoinPracticeTest {


    @Test
    @DisplayName("Movie가 Item을 생성하는지 테스트")
    void movieCreateItem(){

//        JpqlJoinRepository jpqlJoinRepository = new JpqlJoinRepository();
        // given
//        Optional<Movie> findMovie = jpqlJoinRepository.findById(1L);
//        log.info("킄래스  = {}", findMovie.get().getId().getClass());
//        // when
//        Assertions.assertThat(findMovie.get().getId()).isInstanceOf(Item.class);
    }



}