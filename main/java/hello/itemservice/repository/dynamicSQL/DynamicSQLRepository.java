package hello.itemservice.repository.dynamicSQL;

import hello.itemservice.domain.Blog;
import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamicSQLRepository {
    private final DynamicSQLMapper dynamicSQLMapper;

    public List<Blog> findActiveBlogWithTitleLike(Blog blog){
        return dynamicSQLMapper.findActiveBlogWithTitleLike(blog);
    }

    public List<Blog> findActiveBlogLike1(Blog blog){
        return dynamicSQLMapper.findActiveBlogLike1(blog);
    }

    public List<Blog> findActiveBlogLike2(Blog blog){
        return dynamicSQLMapper.findActiveBlogLike2(blog);
    }

    public List<Blog> findActiveBlogLike3(Blog blog){
        return dynamicSQLMapper.findActiveBlogLike3(blog);
    }

    public List<Blog> selectPostIn(Blog blog){
        return dynamicSQLMapper.selectPostIn(blog);
    }

}
