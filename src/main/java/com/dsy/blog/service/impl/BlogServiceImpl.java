package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.mapper.BlogTagMapper;
import com.dsy.blog.mapper.CommentMapper;
import com.dsy.blog.po.Blog;
import com.dsy.blog.po.BlogTag;
import com.dsy.blog.po.Comment;
import com.dsy.blog.po.Tag;
import com.dsy.blog.service.BlogService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created on 2020/4/6
 * Package com.dsy.blog.service.impl
 *
 * @author dsy
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private CommentMapper commentMapper;


    @Override
    public Blog findBlogByBlogId(Integer id) {
        return blogMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public Blog addBlog(Blog blog) {
        int flag ;
        //新增
        if (blog.getBlogId()==null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
            flag= blogMapper.insert(blog);
        }else {   //编辑
            blog.setUpdateTime(new Date());
            flag = blogMapper.updateByPrimaryKeySelective(blog);
            //删除原有的Blog对应的标签值
            Example example = new Example(BlogTag.class);
            example.createCriteria().andEqualTo("blogId",blog.getBlogId());
            blogTagMapper.deleteByExample(example);
        }
        //新增标签值
        if (flag == 1) {
            for (Tag tag : blog.getTags()) {
                BlogTag blogTag = new BlogTag();
                blogTag.setBlogId(blog.getBlogId());
                blogTag.setTagId(tag.getTagId());
                if (blogTagMapper.insert(blogTag) != 1) {
                    return null;
                }
            }
            return blog;
        }
        return null;
    }

    @Override
    public Page<Blog> findAllBlogByPage() {
        List<Blog> select = blogMapper.findBlogAll();
        return (Page<Blog>) select;
    }

    @Transactional
    @Override
    public Integer deleteBlog(Integer id) {
        //删除前将blog_tag表中相关数据删除
        Example exampleBlogTag = new Example(BlogTag.class);
        exampleBlogTag.createCriteria().andEqualTo("blogId",id);
        int i = blogTagMapper.deleteByExample(exampleBlogTag);
        System.out.println("**************************："+i);
        //删除前将comment表中相关数据删除
        Example exampleComment = new Example(Comment.class);
        exampleComment.createCriteria().andEqualTo("blogId",id);
        int j = commentMapper.deleteByExample(exampleComment);
        System.out.println("**************************："+j);
        return blogMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    @Override
    public Blog updateBlog(Blog blog) {
        Blog blog1 = blogMapper.selectByPrimaryKey(blog.getBlogId());
        if (blog1 == null) {
            return null;
        } else {
            int i = blogMapper.updateByPrimaryKey(blog);
            if (i == 1) {
                return blog1;
            }
        }
        return null;
    }

    @Override
    public Page<Blog> selectBlogByKeyWords(String title, String typeId, String recommend) {
        List<Blog> blogs = blogMapper.findBlogByKeyWords(title, typeId, recommend);
        return (Page<Blog>) blogs;
    }

    @Override
    public String findTagsByBlogId(Integer blogId) {
        Example example = new Example(BlogTag.class);
        example.createCriteria().andEqualTo("blogId",blogId);
        List<BlogTag> blogTags = blogTagMapper.selectByExample(example);
        StringBuffer buffer = new StringBuffer();
        for (BlogTag tag: blogTags){
            buffer.append(tag.getTagId()).append(",");
        }
        buffer.deleteCharAt(buffer.length()-1);
        return String.valueOf(buffer);
    }
}
