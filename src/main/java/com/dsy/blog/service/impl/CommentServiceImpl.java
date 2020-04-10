package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.CommentMapper;
import com.dsy.blog.po.Comment;
import com.dsy.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created on 2020/4/10
 * Package com.dsy.blog.service.impl
 *
 * @author dsy
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findCommentsByBlogId(Integer blogId) {
        Example example = new Example(Comment.class);
        example.createCriteria().andEqualTo("blogId", blogId);
        example.setOrderByClause("comment_id desc");
        List<Comment> comments = commentMapper.selectByExample(example);
//        HashMap<Integer,Integer> map = new HashMap<>();
//        List<List<Comment>> commentList = new ArrayList<>(16);
//        for (Comment comment : comments) {
//            if (map.containsKey(comment.getCommentId())){
//                continue;
//            }
//            if (comment.getParentCommentId()!=-1){
//                List<Comment> list = new ArrayList<>();
//                list.add(comment);
//                map.put(comment.getCommentId(),null);
//                while (comment.getParentCommentId()!=-1){
//                    Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentCommentId());
//                    list.add(parentComment);
//                    map.put(parentComment.getCommentId(),null);
//                    comment = parentComment;
//                }
//                list.sort(Comparator.comparingInt(Comment::getCommentId));
//                commentList.add(list);
//            }else {
//                List<Comment> list = new ArrayList<>();
//                list.add(comment);
//                map.put(comment.getCommentId(),null);
//                commentList.add(list);
//            }
//        }
        return comments;
    }

    @Transactional
    @Override
    public void saveComment(Comment comment) {
        comment.setCreateTime(new Date());
        int insert = commentMapper.insert(comment);
        if (insert == 1) {
        }
    }
}
