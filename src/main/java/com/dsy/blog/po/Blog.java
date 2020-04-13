package com.dsy.blog.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created on 2020/4/3
 * Package com.dsy.blog.po
 *
 * @author dsy
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@JsonIgnoreProperties(value = {"handler"})
public class Blog implements Comparable<Blog> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer blogId;
    private Integer typeId;
    private Integer userId;
    private String title;  //标题
    private String content;
    private String firstPicture;
    private String flag;  //标记
    private String description;
    private Integer views;  //浏览次数
    private Boolean appreciation;  //赞赏
    private Boolean shareStatement;  //转载声明
    private Boolean comment;  //评论
    private Boolean publish;  //发布
    private Boolean recommend; //推荐
    private Date createTime;
    private Date updateTime;

    @Transient
    private Type type;

    @Transient
    private User user;

    @Transient
    protected List<Tag> tags;

    @Override
    public int compareTo(Blog o) {
        return o.getUpdateTime().compareTo(this.updateTime);
    }
}
