package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.BlogTagMapper;
import com.dsy.blog.mapper.TagMapper;
import com.dsy.blog.modelEntity.TagTops;
import com.dsy.blog.po.BlogTag;
import com.dsy.blog.po.Tag;
import com.dsy.blog.service.TagService;
import com.dsy.blog.util.RedisKeyUtils;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final Logger log = LoggerFactory.getLogger(TagService.class);

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Tag saveTag(Tag tag) {
        int insert = tagMapper.insert(tag);
        if (insert == 1) {
            return tagMapper.selectByPrimaryKey(tag.getTagId());
        } else {
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
        example.createCriteria().andEqualTo("TagId", id);
        List<BlogTag> blogTags = blogTagMapper.selectByExample(example);
        if (blogTags.size() > 0) {
            return 0;
        }
        int i = tagMapper.deleteByPrimaryKey(id);
        //删除对应的redis中的数据
        redisTemplate.opsForHash().delete(RedisKeyUtils.BLOG_TAGS, id);
        log.info("删除redis中有关TagTops的数据，其hashKey为：" + id);
        return i;
    }

    @Override
    public Tag updateTag(Tag tag) {
        Tag t = tagMapper.selectByPrimaryKey(tag.getTagId());
        if (t==null){
            return null;
        }else {
            int i = tagMapper.updateByPrimaryKey(tag);
            if (i==1) {
                //更新redis中的数据
                TagTops tagTops = (TagTops) redisTemplate.opsForHash().get(RedisKeyUtils.BLOG_TAGS, tag.getTagId());
                log.info("从redis中获取数据：" + tagTops);
                if (tagTops != null) {
                    tagTops.setName(tag.getName());
                    redisTemplate.opsForHash().put(RedisKeyUtils.BLOG_TAGS, tagTops.getTagId(), tagTops);
                    log.info("更新redis中blogTags的相关数据" + tagTops);
                }
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
        //先从redis中查找数据
        List<Object> values = redisTemplate.opsForHash().values(RedisKeyUtils.BLOG_TAGS);
        log.info("从redis中获取数据：" + values);
        if (values.size() > 0) {
            List<TagTops> tagTops = new ArrayList<>();
            for (Object object : values) {
                tagTops.add((TagTops) object);
            }
            tagTops.sort((o1, o2) -> o2.getBlogNumber() - o1.getBlogNumber());
            log.info("从redis中查找到由标签显示博客的数据：" + tagTops);
            return tagTops;
        }
        //redis中没有数据则从mysql中查找数据并存放到redis中
        List<TagTops> severalTopTags = tagMapper.findSeveralTopTags(number);
        for (TagTops tagTops : severalTopTags) {
            redisTemplate.opsForHash().put(RedisKeyUtils.BLOG_TAGS, tagTops.getTagId(), tagTops);
            log.info("将数据加入到redis中：" + tagTops);
        }
        log.info("从Mysql中查找到由标签显示博客的数据：" + severalTopTags);
        return severalTopTags;
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
