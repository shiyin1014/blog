package com.dsy.blog.controller;

import com.dsy.blog.po.Blog;
import com.dsy.blog.service.BlogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping("/")
    public String index(@RequestParam(value = "page",required = false,defaultValue = "1") String page,
                        Model model){
        PageHelper.startPage(Integer.parseInt(page),10);
        Page<Blog> blogPage = blogService.findAllBlogByPage();
        PageInfo<Blog> pageInfo = new PageInfo<>(blogPage);
        model.addAttribute("pageInfo",pageInfo);
        return "index";
    }

    @GetMapping(value = "blog")
    public String blog(){
        return "blog";
    }
}
