package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;
    private String director;
    private String actor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Item id;

    public Movie(){

    }

    public Movie(String director, String actor){
        this.director = director;
        this.actor = actor;
    }

}
