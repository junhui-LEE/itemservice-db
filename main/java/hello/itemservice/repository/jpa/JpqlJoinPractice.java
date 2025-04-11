package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface JpqlJoinPractice extends JpaRepository<Movie, Long> {

}
