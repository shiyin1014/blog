package com.dsy.blog.service;

import com.dsy.blog.po.User;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.service
 *
 * @author dsy
 */
public interface UserService {
    User checkUser(String username,String password);
}
