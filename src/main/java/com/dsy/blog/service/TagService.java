package com.dsy.blog.service;

import com.dsy.blog.modelEntity.TagTops;
import com.dsy.blog.po.Tag;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created on 2020/4/5
 * Package com.dsy.blog.service.impl
 *
 * @author dsy
 */
public interface TagService {

    Tag saveTag(Tag tag);

    Tag getTagByTagName(String name);

    Tag getTagById(Integer id);

    Page<Tag> getTagByPage();

    int deleteTagById(Integer id);

    Tag updateTag(Tag tag);

    List<Tag> finaAllTags();

    List<Tag> listTag(String ids);

    List<TagTops> findSeveralTopTags(int number);

    List<Tag> findTagsByBlogId(Integer blogId);

    Integer findCount();
}
