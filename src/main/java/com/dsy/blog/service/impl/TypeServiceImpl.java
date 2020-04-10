package com.dsy.blog.service.impl;

import com.dsy.blog.exception.NotFoundException;
import com.dsy.blog.mapper.BlogMapper;
import com.dsy.blog.mapper.TypeMapper;
import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.Blog;
import com.dsy.blog.po.Type;
import com.dsy.blog.service.TypeService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.service.impl
 *
 * @author dsy
 */
@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private BlogMapper blogMapper;


    @Transactional
    @Override
    public Type saveType(Type type) {
        Integer integer = typeMapper.insert(type);
        return typeMapper.selectByPrimaryKey(integer);
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
        if (t==null){
            throw new NotFoundException("不存在该类型");
        }else {
            typeMapper.updateByPrimaryKey(type);
        }
        return typeMapper.selectByPrimaryKey(type.getTypeId());
    }

    @Transactional
    @Override
    public int deleteType(Integer id) {
        //先查询blog是否引用过该typeId 若没有则删除，否则删除失败
        Example example = new Example(Blog.class);
        example.createCriteria().andEqualTo("typeId",id);
        List<Blog> blogs = blogMapper.selectByExample(example);
        if (blogs.size()>0){
            return 0;
        }
        return typeMapper.deleteByPrimaryKey(id);
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
        return typeMapper.selectSeveralTopTypes(number);
    }

    @Override
    public Integer findCount() {
        return typeMapper.selectCount(null);
    }


}
