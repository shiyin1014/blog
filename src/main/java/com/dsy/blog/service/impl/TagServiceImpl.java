package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.BlogTagMapper;
import com.dsy.blog.mapper.TagMapper;
import com.dsy.blog.modelEntity.TagTops;
import com.dsy.blog.po.BlogTag;
import com.dsy.blog.po.Tag;
import com.dsy.blog.service.TagService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020/4/5
 * Package com.dsy.blog.service.impl
 *
 * @author dsy
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Override
    public Tag saveTag(Tag tag) {
        int insert = tagMapper.insert(tag);
        if (insert==1){
            return tagMapper.selectByPrimaryKey(tag.getTagId());
        }else {
            return null;
        }
    }

    @Override
    public Tag getTagByTagName(String name) {
        Example example = new Example(Tag.class);
        example.createCriteria().andEqualTo("name",name);
        return tagMapper.selectOneByExample(example);
    }

    @Override
    public Tag getTagById(Integer id) {
        return tagMapper.selectByPrimaryKey(id);
    }

    @Override
    public Page<Tag> getTagByPage() {
        return (Page<Tag>) tagMapper.selectAll();
    }

    @Override
    public int deleteTagById(Integer id) {
        //先查询blog是否引用过该typeId 若没有则删除，否则删除失败
        Example example = new Example(BlogTag.class);
        example.createCriteria().andEqualTo("tagId",id);
        List<BlogTag> blogTags = blogTagMapper.selectByExample(example);
        if (blogTags.size()>0){
            return 0;
        }
        return tagMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Tag updateTag(Tag tag) {
        Tag t = tagMapper.selectByPrimaryKey(tag.getTagId());
        if (t==null){
            return null;
        }else {
            int i = tagMapper.updateByPrimaryKey(tag);
            if (i==1){
                return tag;
            }
        }
        return null;
    }

    @Override
    public List<Tag> finaAllTags() {
        return tagMapper.selectAll();
    }

    @Override
    public List<Tag> listTag(String ids) {
        String[] split = ids.split(",");
        List<Tag> tags = new ArrayList<>();
        for (String tag : split) {
            Tag selectByPrimaryKey = tagMapper.selectByPrimaryKey(tag);
            tags.add(selectByPrimaryKey);
        }
        return tags;
    }

    @Override
    public List<TagTops> findSeveralTopTags(int number) {
        return tagMapper.findSeveralTopTags(number);
    }

    @Override
    public List<Tag> findTagsByBlogId(Integer blogId) {
        Example example = new Example(BlogTag.class);
        example.createCriteria().andEqualTo("blogId", blogId);
        List<BlogTag> blogTags = blogTagMapper.selectByExample(example);
        List<Tag> list = new ArrayList<>();
        for (BlogTag blogTag : blogTags) {
            Tag tag = tagMapper.selectByPrimaryKey(blogTag.getTagId());
            list.add(tag);
        }
        return list;
    }

    @Override
    public Integer findCount() {
        return tagMapper.selectCount(null);
    }
}
