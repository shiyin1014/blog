package com.dsy.blog.mapper;

import com.dsy.blog.po.Blog;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created on 2020/4/5
 * Package com.dsy.blog.mapper
 *
 * @author dsy
 */
public interface BlogMapper extends Mapper<Blog>{

    @Select("select * from blog order by update_time desc")
    @Results(id = "blogMap",
            value = {
            @Result(column = "title",property = "title"),
            @Result(column = "content",property = "content"),
            @Result(column = "first_picture",property = "firstPicture"),
            @Result(column = "flag",property = "flag"),
            @Result(column = "views",property = "views"),
                    @Result(column = "appreciation", property = "appreciation"),
                    @Result(column = "share_statement", property = "shareStatement"),
                    @Result(column = "comment", property = "comment"),
                    @Result(column = "publish", property = "publish"),
                    @Result(column = "recommend", property = "recommend"),
                    @Result(column = "update_time", property = "updateTime"),
                    @Result(column = "create_time", property = "createTime"),
                    @Result(column = "type_id", property = "type",
                            one = @One(select = "com.dsy.blog.mapper.TypeMapper.selectTypeById", fetchType = FetchType.LAZY)),
                    @Result(column = "user_id", property = "user",
                            one = @One(select = "com.dsy.blog.mapper.UserMapper.selectUserByUserId", fetchType = FetchType.LAZY))
            })
    List<Blog> findBlogAll();


    List<Blog> findBlogByKeyWords(@Param(value = "title") String title,
                                  @Param(value = "typeId") String typeId,
                                  @Param(value = "recommend") String recommend);


    @Select("select * from blog where blog_id = #{id}")
    @ResultMap(value = "blogMap")
    Blog selectBlogByBlogId(@Param(value = "id") String id);

    @Select("select * from blog where type_id = #{typeId}")
    @ResultMap(value = "blogMap")
    List<Blog> selectBlogByTypeId(@Param(value = "typeId") Integer typeId);


}
