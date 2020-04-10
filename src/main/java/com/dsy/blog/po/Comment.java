package com.dsy.blog.po;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

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
public class Comment implements Comparable<Comment> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;
    private Integer blogId;
    private String nickName;
    private String email;
    private String content;
    private String avatar;
    private Date createTime;
    private Integer parentCommentId;

    @Override
    public int compareTo(Comment o) {
        return this.commentId - o.commentId;
    }
}
