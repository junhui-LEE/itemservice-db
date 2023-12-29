package hello.itemservice.domain;

import lombok.Data;

@Data
public class Blog {
    private Long id;
    private String state;
    private String title;
    private String authorName;
    private Integer featured;

    public Blog() {
    }
    public Blog(String state, String title, String authorName, Integer featured) {
        this.state = state;
        this.title = title;
        this.authorName = authorName;
        this.featured = featured;
    }
}
