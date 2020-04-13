package com.dsy.blog.config;

import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.po.Blog;
import com.dsy.blog.util.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2020/4/13
 * Package com.dsy.blog.config
 *
 * @author dsy
 */
@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {
    @Autowired
    private BlogMapper BlogMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        List<Blog> list = BlogMapper.findBlogAll();
        for (Blog blog : list) {
            redisTemplate.opsForHash().put(RedisKeyUtils.ALL_BLOG, blog.getBlogId(), blog);
            log.info("将blog数据添加到redis的AllBlog中，其博客标题为：" + blog.getTitle());
        }
        redisTemplate.opsForValue().set(RedisKeyUtils.BLOG_COUNT, list.size());
        log.info("初始化redis中blogCount的数量为：" + list.size());
    }
}
