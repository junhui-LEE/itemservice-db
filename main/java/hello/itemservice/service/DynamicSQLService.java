package hello.itemservice.service;

import hello.itemservice.domain.Blog;
import hello.itemservice.repository.dynamicSQL.DynamicSQLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DynamicSQLService {

    private final DynamicSQLRepository dynamicSQLRepository;
    public List<Blog> findActiveBlogWithTitleLike(Blog blog){
        return dynamicSQLRepository.findActiveBlogWithTitleLike(blog);
    }

    public List<Blog> findActiveBlogLike1(Blog blog){
        return dynamicSQLRepository.findActiveBlogLike1(blog);
    }

    public List<Blog> findActiveBlogLike2(Blog blog){
        return dynamicSQLRepository.findActiveBlogLike2(blog);
    }

    public List<Blog> findActiveBlogLike3(Blog blog){
        return dynamicSQLRepository.findActiveBlogLike3(blog);
    }

    public List<Blog> selectPostIn(Blog blog){
        return dynamicSQLRepository.selectPostIn(blog);
    }


}
