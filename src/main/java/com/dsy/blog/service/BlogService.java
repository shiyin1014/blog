package com.dsy.blog.service;

import com.dsy.blog.po.Blog;
import com.dsy.blog.po.BlogTag;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created on 2020/4/5
 * Package com.dsy.blog.service
 *
 * @author dsy
 */
public interface BlogService {

    Blog findBlogByBlogId(Integer id);

    Blog addBlog(Blog blog);

    Page<Blog> findAllBlogByPage();

    Integer deleteBlog(Integer id);

    Blog updateBlog(Blog blog);

    Page<Blog> selectBlogByKeyWords(String title, String typeId, String recommend);

    String findTagsByBlogId(Integer blogId);

    List<Blog> findTheLastBlog(int i);

    Page<Blog> findBlogByKeyWords(String key);
}
