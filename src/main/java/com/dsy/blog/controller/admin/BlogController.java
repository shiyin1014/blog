package com.dsy.blog.controller.admin;

import com.dsy.blog.po.Blog;
import com.dsy.blog.po.Type;
import com.dsy.blog.po.User;
import com.dsy.blog.service.BlogService;
import com.dsy.blog.service.TagService;
import com.dsy.blog.service.TypeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.controller.admin
 *
 * @author dsy
 */
@Controller
@RequestMapping(value = "/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT = "redirect:/admin/blogs";


    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("blogs")
    public String blogs(Model model, @RequestParam(required = false, defaultValue = "1") String page) {
        PageHelper.startPage(Integer.parseInt(page), 8);
        List<Blog> allBlogByPage = blogService.findAllBlogByPage();
        PageInfo<Blog> pageInfo = new PageInfo<>(allBlogByPage);
        model.addAttribute("pageInfo", pageInfo);
        List<Type> types = typeService.allType();
        model.addAttribute("types", types);
        model.addAttribute("flag", 1);
        return LIST;
    }

    @PostMapping("blogs/search")
    public String search(Model model, @RequestParam(required = false, defaultValue = "1") String page,
                         String title, String typeId, Boolean recommend) {
        PageHelper.startPage(Integer.parseInt(page), 8);
        Page<Blog> blogPage = blogService.selectBlogByKeyWords(title.equalsIgnoreCase("") ? null : title
                , typeId.equalsIgnoreCase("") ? null : typeId, recommend ? "1" : null);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogPage);
        model.addAttribute("pageInfo", pageInfo);
        return "admin/blogs :: blogList";
    }

    /**
     * 跳转增加博客页面
     * @param model
     * @return
     */
    @GetMapping("blogs/addPage")
    public String addPage(Model model) {
        model.addAttribute("blog",new Blog());
        model.addAttribute("types", typeService.allType());
        model.addAttribute("tags", tagService.finaAllTags());
        return INPUT;
    }


    /**
     * 跳转到修改博客页面
     * @param model
     * @return
     */
    @GetMapping("blogs/{id}/edit")
    public String editPage(Model model, @PathVariable String id) {
        Blog blog = blogService.findBlogByBlogId(Integer.valueOf(id));
        if (blog==null){
            return "/error/404";
        }
        model.addAttribute("blog",blog);
        model.addAttribute("tagIds",blogService.findTagsByBlogId(Integer.valueOf(id)));
        model.addAttribute("types", typeService.allType());
        model.addAttribute("tags", tagService.finaAllTags());
        return INPUT;
    }

    /**
     * 删除博客
     * @param id 博客id
     * @return
     */
    @GetMapping(value = "blogs/{id}/delete")
    public String deleteBlog(@PathVariable String id,RedirectAttributes attributes){
        Integer integer = blogService.deleteBlog(Integer.valueOf(id));
        if (integer==1){
            attributes.addFlashAttribute("message","删除成功");
        }else {
            attributes.addFlashAttribute("message","删除失败");
        }
        return REDIRECT;

    }


    /**
     * 添加(修改并用)博客
     * @param blog 封装博客属性
     * @param session session
     * @param attributes 用于重定向设置属性，前端获取
     * @param tagIds 博客有关的标签
     * @return
     */
    @PostMapping(value = "/blogs/add")
    public String blogAdd(Blog blog, HttpSession session, RedirectAttributes attributes, String tagIds) {
        User user = (User) session.getAttribute("user");
        blog.setUser(user);
        blog.setRecommend(blog.getRecommend() != null);
        blog.setAppreciation(blog.getAppreciation()!=null);
        blog.setShareStatement(blog.getShareStatement()!=null);
        blog.setComment(blog.getComment() != null);
        blog.setType(typeService.getType(blog.getTypeId()));
        blog.setTags(tagService.listTag(tagIds));
        blog.setUserId(user.getUserId());
        blog.setTypeId(blog.getTypeId());
        Blog addBlog = blogService.addBlog(blog);
        if (addBlog != null) {
            attributes.addFlashAttribute("message", "操作成功");
        } else {
            attributes.addFlashAttribute("message", "操作失败");
        }
        return "redirect:/admin/blogs";
    }


}
