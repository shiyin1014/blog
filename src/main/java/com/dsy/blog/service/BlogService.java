package com.dsy.blog.service;

import com.dsy.blog.po.Blog;
import com.github.pagehelper.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 2020/4/5
 * Package com.dsy.blog.service
 *
 * @author dsy
 */
public interface BlogService {

    Blog findBlogByBlogId(Integer id);

    Blog addBlog(Blog blog);

    List<Blog> findAllBlogByPage();

    Integer deleteBlog(Integer id);

    Page<Blog> selectBlogByKeyWords(String title, String typeId, String recommend);

    String findTagsByBlogId(Integer blogId);

    List<Blog> findTheLastBlog(int i);

    Page<Blog> findBlogByKeyWords(String key);

    List<Blog> findBlogByTypeId(Integer typeId);

    List<Blog> findBlogByTagId(Integer tagId);

    Map<String, List<Blog>> findArchiveBlog();

    Integer findBlogCount();

    void addBlogViews(Blog blog);
}
