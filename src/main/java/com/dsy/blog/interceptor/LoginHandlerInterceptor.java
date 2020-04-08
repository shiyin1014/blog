package com.dsy.blog.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.intercepter
 *
 * @author dsy
 */

public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object admin = session.getAttribute("user");
        if (admin==null){
            request.setAttribute("message","没有权限，请先登录");
            request.getRequestDispatcher("/admin").forward(request,response);
        }
        return true;
    }
}
