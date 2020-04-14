package com.dsy.blog.service.impl;

import com.dsy.blog.exception.NotFoundException;
import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.mapper.TypeMapper;
import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.Blog;
import com.dsy.blog.po.Type;
import com.dsy.blog.service.TypeService;
import com.dsy.blog.util.RedisKeyUtils;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.service.impl
 *
 * @author dsy
 */
@Service
@Slf4j
public class TypeServiceImpl implements TypeService {


    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public Type saveType(Type type) {
        int integer = typeMapper.insert(type);
        return integer == 1 ? type : null;
    }

    @Override
    public Type getType(Integer typeId) {
        return typeMapper.selectByPrimaryKey(typeId);
    }

    @Transactional
    @Override
    public Page<Type> listType() {
        return (Page<Type>) typeMapper.selectAll();
    }

    @Transactional
    @Override
    public Type updateType(Type type) {
        Type t = typeMapper.selectByPrimaryKey(type.getTypeId());
        if (t == null) {
            throw new NotFoundException("不存在该类型");
        } else {
            typeMapper.updateByPrimaryKey(type);
            //更新redis中的数据
            TypeTops typeTops = (TypeTops) redisTemplate.opsForHash().get(RedisKeyUtils.BLOG_TYPES, type.getTypeId());
            log.info("从redis中获得数据：" + typeTops);
            if (typeTops != null) {
                typeTops.setName(type.getName());
                redisTemplate.opsForHash().put(RedisKeyUtils.BLOG_TYPES, typeTops.getTypeId(), typeTops);
                log.info("更新redis中的数据：" + typeTops);
            }
        }
        return typeMapper.selectByPrimaryKey(type.getTypeId());
    }

    @Transactional
    @Override
    public int deleteType(Integer id) {
        //先查询blog是否引用过该typeId 若没有则删除，否则删除失败
        Example example = new Example(Blog.class);
        example.createCriteria().andEqualTo("typeId", id);
        List<Blog> blogs = blogMapper.selectByExample(example);
        if (blogs.size() > 0) {
            return 0;
        }
        int i = typeMapper.deleteByPrimaryKey(id);
        //从redis中删除BlogTypes对应的数据
        redisTemplate.opsForHash().delete(RedisKeyUtils.BLOG_TYPES, id);
        log.info("删除redis中与BlogTypes对应的类型的数据，类型id为：" + id);
        return i;
    }

    @Override
    public Type getTypeByName(String typeName) {
        Example example = new Example(Type.class);
        example.createCriteria().andEqualTo("name", typeName);
        return typeMapper.selectOneByExample(example);
    }

    @Override
    public List<Type> allType() {
        return typeMapper.selectAll();
    }

    @Override
    public List<TypeTops> findSeveralTypes(Integer number) {
        //先从redis数据库中获取
        List<Object> objectList = redisTemplate.opsForHash().values(RedisKeyUtils.BLOG_TYPES);
        if (objectList.size() > 0) {
            List<TypeTops> list = new ArrayList<>();
            for (Object o : objectList) {
                list.add((TypeTops) o);
            }
            //对list进行排序,按照类型对应的博客数量递减
            list.sort((o1, o2) -> o2.getBlogNumber() - o1.getBlogNumber());
            return list;
        }
        //没有再从mysql数据库中获得并放到redis中
        List<TypeTops> typeTops = typeMapper.selectSeveralTopTypes(number);
        for (TypeTops typeTop : typeTops) {
            //存入redis
            redisTemplate.opsForHash().put(RedisKeyUtils.BLOG_TYPES, typeTop.getTypeId(), typeTop);
        }
        return typeTops;
    }


    @Override
    public Integer findCount() {
        return typeMapper.selectCount(null);
    }

}
