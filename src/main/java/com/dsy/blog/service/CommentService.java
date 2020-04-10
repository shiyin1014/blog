package com.dsy.blog.service;

import com.dsy.blog.po.Comment;

import java.util.List;

/**
 * Created on 2020/4/10
 * Package com.dsy.blog.service
 *
 * @author dsy
 */
public interface CommentService {

    List<Comment> findCommentsByBlogId(Integer blogId);

    void saveComment(Comment comment);
}
