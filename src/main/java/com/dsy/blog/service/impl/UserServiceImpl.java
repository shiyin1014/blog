package com.dsy.blog.service.impl;

import com.dsy.blog.mapper.UserMapper;
import com.dsy.blog.po.User;
import com.dsy.blog.service.UserService;
import com.dsy.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.service
 *
 * @author dsy
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User checkUser(String username, String password) {
        String pwd = MD5Util.md5(password);
        return userMapper.selectUserNameByNameAndPassWord(username,pwd);
    }
}
