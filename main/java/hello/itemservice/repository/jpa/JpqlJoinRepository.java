package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpqlJoinRepository {

    private final JpqlJoinPractice jpqlJoinPractice;

    public Optional<Movie> findById(Long movie_id){
        return jpqlJoinPractice.findById(movie_id);
    }


}
