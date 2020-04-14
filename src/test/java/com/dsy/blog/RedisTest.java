package com.dsy.blog;

import com.dsy.blog.modelEntity.TagTops;
import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.Blog;
import com.dsy.blog.po.Type;
import com.dsy.blog.po.User;
import com.dsy.blog.service.BlogService;
import com.dsy.blog.service.TagService;
import com.dsy.blog.service.TypeService;
import com.dsy.blog.util.RedisKeyUtils;
import com.github.pagehelper.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020/4/12
 * Package com.dsy.blog
 *
 * @author dsy
 */
@SpringBootTest
public class RedisTest {

//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    private TypeService typeService;
//
//    @Autowired
//    private TagService tagService;
//
//    @Autowired
//    private BlogService blogService;
//
//    @Test
//    public void testConnectionRedis() {
//        System.out.println(redisTemplate);
//    }
//
//    /**
//     * opsForValue
//     */
//    @Test
//    public void testSetAndGetUserOfRedis() {
//        User user = new User();
//        user.setUserId(1);
//        user.setUsername("dsy");
//        user.setPassword("admin");
//        redisTemplate.opsForValue().set("user:" + 2, user);
//        System.out.println(redisTemplate.opsForValue().get("user:2"));
//    }
//
//    @Test
//    public void testHash() {
////        User user = new User();
////        user.setUserId(2);
////        user.setUsername("aaaa");
////        user.setPassword("admin");
////        redisTemplate.opsForHash().put("user",user.getUserId(),user);
////        System.out.println(redisTemplate.opsForHash().values("user"));
//
////        redisTemplate.opsForHash().put("tags",1,"aaa");
////        redisTemplate.opsForHash().put("tags",2,"bbb");
//        redisTemplate.delete("tags");
//    }
//
//    @Test
//    public void testCount() {
//        redisTemplate.opsForValue().set("BlogCount", 10);
//        redisTemplate.opsForValue().increment("BlogCount");
//    }
//
//    @Test
//    public void findSeveralTypes() {
//        List<TypeTops> types = typeService.findSeveralTypes(5);
//        System.out.println(types);
//    }
//
//    @Test
//    public void testList() {
//        redisTemplate.opsForList().leftPush("types", "aaa");
//        redisTemplate.opsForList().leftPush("types", "bbb");
//        redisTemplate.opsForList().leftPush("types", "ccc");
//        List<Object> types = redisTemplate.opsForList().range("types", 0, -1);
//        System.out.println(types);
//
//    }
//
//
//    @Test
//    public void findSeveralTopTags() {
//        tagService.findSeveralTopTags(9);
//    }
//
//
//    @Test
//    public void findTheLastBlog() {
//        List<Blog> theLastBlog = blogService.findTheLastBlog(5);
//        System.out.println(theLastBlog);
//        System.out.println(theLastBlog.size());
//    }
//
//
//
//    @Test
//    public void testHashMoreObject() {
//        Blog blog = new Blog();
//        blog.setBlogId(1);
//        Type type = new Type();
//        type.setTypeId(2);
//        blog.setType(type);
////        redisTemplate.opsForHash().put("users",blog.getBlogId(),blog);
//        Object users = redisTemplate.opsForHash().get("users", blog.getBlogId());
//        System.out.println(users);
//    }

}
