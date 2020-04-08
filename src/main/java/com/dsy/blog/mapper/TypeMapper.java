package com.dsy.blog.mapper;

import com.dsy.blog.po.Type;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.mapper
 *
 * @author dsy
 */
public interface TypeMapper extends Mapper<Type> {

    @Select("select * from type where type_id = #{id}")
    Type selectTypeById(@Param(value = "id") String id);
}
