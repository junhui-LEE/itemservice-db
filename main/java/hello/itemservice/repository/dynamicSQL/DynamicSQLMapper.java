package hello.itemservice.repository.dynamicSQL;

import hello.itemservice.domain.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DynamicSQLMapper {
    List<Blog> findActiveBlogWithTitleLike(Blog blog);

    List<Blog> findActiveBlogLike1(Blog blog);

    List<Blog> findActiveBlogLike2(Blog blog);

    List<Blog> findActiveBlogLike3(Blog blog);

    List<Blog> selectPostIn(Blog blog);
}
