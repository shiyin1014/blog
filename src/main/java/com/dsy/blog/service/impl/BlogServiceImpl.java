package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.mapper.BlogTagMapper;
import com.dsy.blog.mapper.CommentMapper;
import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.*;
import com.dsy.blog.service.BlogService;
import com.dsy.blog.service.TagService;
import com.dsy.blog.util.RedisKeyUtils;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final Logger log = LoggerFactory.getLogger(BlogService.class);

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Blog findBlogByBlogId(Integer id) {
        //尝试从redis中获取
        Blog blog = (Blog) redisTemplate.opsForHash().get(RedisKeyUtils.ALL_BLOG, id);
        if (blog != null) {
            log.info("从redis中获取Blog的数据，标题为" + blog.getTitle());
            return blog;
        } else {
            Blog selectBlogByBlogId = blogMapper.selectBlogByBlogId(String.valueOf(id));
            //存入redis
            redisTemplate.opsForHash().put(RedisKeyUtils.ALL_BLOG, selectBlogByBlogId.getBlogId(), selectBlogByBlogId);
            log.info("将blog信息存入redis中，标题为" + selectBlogByBlogId.getTitle());
            return selectBlogByBlogId;
        }
    }

    @Transactional
    @Override
    public Blog addBlog(Blog blog) {
        int flag ;
        //新增
        if (blog.getBlogId()==null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
            flag = blogMapper.insert(blog);
            if (flag == 1) {
                //更新redis数据库中的BlogCount属性值
                redisTemplate.opsForValue().increment(RedisKeyUtils.BLOG_COUNT);
                log.info("更新redis中BlogCount的数值,变化为：" + "+1");
                //更新redis中BlogTypes中相关的数据
                TypeTops typeTops = (TypeTops) redisTemplate.opsForHash().get(RedisKeyUtils.BLOG_TYPES, blog.getTypeId());
                log.info("更新redis中BlogTypes中相关的数据:" + typeTops);
                if (typeTops != null) {
                    typeTops.setBlogNumber(typeTops.getBlogNumber() + 1);
                    redisTemplate.opsForHash().put(RedisKeyUtils.BLOG_TYPES, typeTops.getTypeId(), typeTops);
                    log.info("更新redis中BlogTypes中相关的数据" + typeTops);
                }
            }
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
            //更新redis中BlogTags中相关的数据(由于一个博客可能对应无数个标签，故在此将redis中的BlogTags的key删除)
            redisTemplate.delete(RedisKeyUtils.BLOG_TAGS);
            log.info("删除redis中key为blogtags的hash数据类型");
            Blog blog1 = blogMapper.selectByPrimaryKey(blog.getBlogId());
            blog.setCreateTime(blog1.getCreateTime());
            blog.setViews(blog1.getViews());
            //更新TheLatestBlog数据
            redisTemplate.opsForHash().put(RedisKeyUtils.THE_LATEST_BLOG, blog.getBlogId(), blog);
            log.info("更新redis中TheLatestBlog的数据，标题为" + blog.getTitle());
            //更新AllBlog数据
            redisTemplate.opsForHash().put(RedisKeyUtils.ALL_BLOG, blog.getBlogId(), blog);
            log.info("更新redis中AllBlog的数据，标题为" + blog.getTitle());
            return blog;
        }
        return null;
    }

    @Override
    public List<Blog> findAllBlogByPage() {
        return blogMapper.findBlogAll();
    }

    @Transactional
    @Override
    public Integer deleteBlog(Integer id) {
        Blog blog = blogMapper.selectByPrimaryKey(id);
        //删除前将blog_tag表中相关数据删除
        Example exampleBlogTag = new Example(BlogTag.class);
        exampleBlogTag.createCriteria().andEqualTo("blogId", id);
        blogTagMapper.deleteByExample(exampleBlogTag);
        //删除前将comment表中相关数据删除
        Example exampleComment = new Example(Comment.class);
        exampleComment.createCriteria().andEqualTo("blogId", id);
        commentMapper.deleteByExample(exampleComment);
        int delete = blogMapper.deleteByPrimaryKey(id);
        if (delete == 1) {
            //更新redis数据库中的BlogCount属性值
            redisTemplate.opsForValue().decrement(RedisKeyUtils.BLOG_COUNT);
            log.info("更新redis中BlogCount的数值,变化为：" + "-1");
            //更新redis中BlogTypes中相关的数据
            TypeTops typeTops = (TypeTops) redisTemplate.opsForHash().get(RedisKeyUtils.BLOG_TYPES, blog.getTypeId());
            log.info("从redis中获取到typeTops的数据：" + typeTops);
            if (typeTops != null) {
                typeTops.setBlogNumber(typeTops.getBlogNumber() - 1);
                redisTemplate.opsForHash().put(RedisKeyUtils.BLOG_TYPES, typeTops.getTypeId(), typeTops);
                log.info("更新redis中BlogTypes中相关的数据" + typeTops);
            }
            //更新redis中BlogTags中相关的数据(由于一个博客可能对应无数个标签，故在此将redis中的BlogTags的key删除)
            redisTemplate.delete(RedisKeyUtils.BLOG_TAGS);
            log.info("删除redis中key为blogtags的hash数据类型");
            //删除redis中TheLatestBlog中的数据
            redisTemplate.opsForHash().delete(RedisKeyUtils.THE_LATEST_BLOG, blog.getBlogId());
            log.info("删除redis中TheLatestBlog中有关该博客的数据，标题为" + blog.getTitle());
            redisTemplate.opsForHash().delete(RedisKeyUtils.ALL_BLOG, blog.getBlogId());
            log.info("删除redis中AllBlog中有关该博客的数据，标题为" + blog.getTitle());
        }
        return delete;
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
        //先尝试从redis中获取
        List<Object> values = redisTemplate.opsForHash().values(RedisKeyUtils.THE_LATEST_BLOG);
        log.info("从redis中获取最新博客");
        if (values.size() >= 8 || values.size() >= i) {
            List<Blog> blogs = new ArrayList<>();
            for (Object object : values) {
                blogs.add((Blog) object);
            }
            blogs.sort((o1, o2) -> o2.getUpdateTime().compareTo(o1.getUpdateTime()));
            if (blogs.size() >= i - 1) {
                blogs.subList(0, i - 1);
            }
            return blogs;
        }
        //redis中不存在再从mysql数据库中获取
        Example example = new Example(Blog.class);
        example.setOrderByClause("update_time desc limit " + i);
        List<Blog> blogList = blogMapper.selectByExample(example);
        for (Blog blog : blogList) {
            //存入redis
            redisTemplate.opsForHash().put(RedisKeyUtils.THE_LATEST_BLOG, blog.getBlogId(), blog);
            log.info("将blog数据添加到TheLatestBlog中，标题为" + blog.getTitle());
        }
        return blogList;
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
        //先从redis中取得数据，若不存在再从mysql中获取数据
        Integer count = (Integer) redisTemplate.opsForValue().get(RedisKeyUtils.BLOG_COUNT);
        log.info("从redis中获取到博客总数量为：" + count);
        if (count != null) {
            return count;
        }
        return blogMapper.selectCount(null);
    }

    @Override
    public void addBlogViews(Blog blog) {
        blogMapper.updateByPrimaryKeySelective(blog);
        redisTemplate.opsForHash().put(RedisKeyUtils.ALL_BLOG, blog.getBlogId(), blog);
        log.info("更新redis中AllBlog的博客标题为：" + blog.getTitle() + "的views为：" + blog.getViews());
    }
}
