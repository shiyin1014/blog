package com.dsy.blog.controller;

import com.dsy.blog.modelEntity.TagTops;
import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.Blog;
import com.dsy.blog.po.Tag;
import com.dsy.blog.service.BlogService;
import com.dsy.blog.service.TagService;
import com.dsy.blog.service.TypeService;
import com.dsy.blog.util.MarkDownUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created on 2020/4/2
 * Package com.dsy.blog.controller
 *
 * @author dsy
 */
@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @RequestMapping("/")
    public String index(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                        Model model) {
        PageHelper.startPage(Integer.parseInt(page), 5);
        List<Blog> blogPage = blogService.findAllBlogByPage();
        PageInfo<Blog> pageInfo = new PageInfo<>(blogPage);
        model.addAttribute("pageInfo", pageInfo);
        //type
        List<TypeTops> typeTops = typeService.findSeveralTypes(6);
        model.addAttribute("typeList", typeTops);
        //tags
        List<TagTops> topTags = tagService.findSeveralTopTags(10);
        model.addAttribute("tagList", topTags);
        //the latest recommend
        List<Blog> blogList = blogService.findTheLastBlog(8);
        model.addAttribute("blogList", blogList);
        return "index";
    }

    /**
     * 首页search博客
     *
     * @param key   关键词
     * @param page  页码
     * @param model 视图
     * @return
     */
    @PostMapping("/blog/search")
    public String searchBlogByKeyWords(@RequestParam(value = "key") String key,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") String page,
                                       Model model) {
        PageHelper.startPage(Integer.parseInt(page), 5);
        Page<Blog> blogPage = blogService.findBlogByKeyWords(key);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogPage);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("key", key);
        return "search";
    }

    /**
     * 根据博客id查询博客详情信息
     *
     * @param id 博客id
     * @return
     */
    @GetMapping(value = "/blog/{id}")
    public String blog(@PathVariable String id, Model model) {
        Blog blog = blogService.findBlogByBlogId(Integer.valueOf(id));
        //增加浏览次数
        blog.setViews(blog.getViews() + 1);
        blogService.addBlogViews(blog);
        blog.setContent(MarkDownUtils.markdownToHtmlExtensions(blog.getContent()));
        model.addAttribute("blog", blog);
        List<Tag> tags = tagService.findTagsByBlogId(blog.getBlogId());
        model.addAttribute("tags", tags);
        return "blog";
    }


    @GetMapping(value = "/footer/newBlogs")
    public String newBlogs(Model model) {
        model.addAttribute("newBlogs", blogService.findTheLastBlog(3));
        return "_fragments :: newBlogList";
    }

}
