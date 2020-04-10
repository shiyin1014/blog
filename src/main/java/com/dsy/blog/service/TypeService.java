package com.dsy.blog.service;

import com.dsy.blog.modelEntity.TypeTops;
import com.dsy.blog.po.Type;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.service
 *
 * @author dsy
 */
public interface TypeService {

    Type saveType(Type type);

    Type getType(Integer typeId);

    Page<Type> listType();

    Type updateType(Type type);

    int deleteType(Integer id);

    Type getTypeByName(String typeName);

    List<Type> allType();

    List<TypeTops> findSeveralTypes(Integer number);

    Integer findCount();
}

