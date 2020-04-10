package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.mapper.BlogTagMapper;
import com.dsy.blog.mapper.CommentMapper;
import com.dsy.blog.po.*;
import com.dsy.blog.service.BlogService;
import com.dsy.blog.service.TagService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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

    @Autowired
    private TagService tagService;


    @Override
    public Blog findBlogByBlogId(Integer id) {
        return blogMapper.selectBlogByBlogId(String.valueOf(id));
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
        example.createCriteria().andEqualTo("blogId", blogId);
        List<BlogTag> blogTags = blogTagMapper.selectByExample(example);
        StringBuffer buffer = new StringBuffer();
        for (BlogTag tag : blogTags) {
            buffer.append(tag.getTagId()).append(",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return String.valueOf(buffer);
    }

    @Override
    public List<Blog> findTheLastBlog(int i) {
        Example example = new Example(Blog.class);
        example.setOrderByClause("update_time desc limit " + i);
        return blogMapper.selectByExample(example);
    }

    @Override
    public Page<Blog> findBlogByKeyWords(String key) {
//        Example example = new Example(Blog.class);
//        example.createCriteria().andLike("title","%"+key+"%");
        return (Page<Blog>) blogMapper.findBlogByKeyWords(key, null, null);
    }

    @Override
    public List<Blog> findBlogByTypeId(Integer typeId) {
        List<Blog> blogList = blogMapper.selectBlogByTypeId(typeId);
        for (Blog blog : blogList) {
            List<Tag> tags = tagService.findTagsByBlogId(blog.getBlogId());
            blog.setTags(tags);
        }
        return blogList;
    }

    @Override
    public List<Blog> findBlogByTagId(Integer tagId) {
        List<Blog> blogList = new ArrayList<>();
        Example example = new Example(BlogTag.class);
        example.createCriteria().andEqualTo("TagId", tagId);
        List<BlogTag> blogTags = blogTagMapper.selectByExample(example);
        for (BlogTag blogTag : blogTags) {
            Blog blog = blogMapper.selectBlogByBlogId(String.valueOf(blogTag.getBlogId()));
            List<Tag> tags = tagService.findTagsByBlogId(blog.getBlogId());
            blog.setTags(tags);
            blogList.add(blog);
        }
        return blogList;
    }

    @Override
    public Map<String, List<Blog>> findArchiveBlog() {
        Map<String, List<Blog>> map = new HashMap<>(16);
        List<String> years = blogMapper.findYearsGroupByYear();
        for (String year : years) {
            List<Blog> list = blogMapper.selectBlogByYear(year);
            map.put(year, list);
        }
        return map;
    }

    @Override
    public Integer findBlogCount() {
        return blogMapper.selectCount(null);
    }
}
