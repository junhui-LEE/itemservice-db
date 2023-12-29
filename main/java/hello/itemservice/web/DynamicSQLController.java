package hello.itemservice.web;

import hello.itemservice.domain.Blog;
import hello.itemservice.service.DynamicSQLService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DynamicSQLController {
    private final DynamicSQLService dynamicSQLService;

    @GetMapping("/dynamicSQL")
    public String home() {
        return "dynamicSQLAddForm";
    }

    @PostMapping("/dynamicSQL")
    public String home(@ModelAttribute Blog blog, Model model) {
//        List<Blog> blogs = dynamicSQLService.findActiveBlogWithTitleLike(blog);
//        model.addAttribute("blogs", blogs);
//        return "dynamicSQLResult";

        List<Blog> blogs = dynamicSQLService.findActiveBlogLike1(blog);
        model.addAttribute("blogs", blogs);
        return "dynamicSQLResult";

//        List<Blog> blogs = dynamicSQLService.findActiveBlogWithTitleLike(blog);
//        model.addAttribute("blogs", blogs);
//        return "dynamicSQLResult";
//
//        List<Blog> blogs = dynamicSQLService.findActiveBlogWithTitleLike(blog);
//        model.addAttribute("blogs", blogs);
//        return "dynamicSQLResult";
//
//        List<Blog> blogs = dynamicSQLService.findActiveBlogWithTitleLike(blog);
//        model.addAttribute("blogs", blogs);
//        return "dynamicSQLResult";

    }

}
