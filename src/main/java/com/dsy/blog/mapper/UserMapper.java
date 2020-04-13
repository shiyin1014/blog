package com.dsy.blog.mapper;


import com.dsy.blog.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.mapper
 *
 * @author dsy
 */
public interface UserMapper extends Mapper<User> {

    @Select("select * from user where username = #{username} and password = #{password}")
    User selectUserNameByNameAndPassWord(String username,String password);


    @Select("select * from user where user_id = #{id}")
    @ResultType(User.class)
    User selectUserByUserId(@Param(value = "id") String id);

}
