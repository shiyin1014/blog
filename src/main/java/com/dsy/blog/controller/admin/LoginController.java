package com.dsy.blog.controller.admin;

import com.dsy.blog.po.User;
import com.dsy.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * Created on 2020/4/4
 * Package com.dsy.blog.controller.admin
 *
 * @author dsy
 */
@Controller
@RequestMapping(value = "/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping
    public String loginPage(){
        return "admin/login";
    }

    @PostMapping(value = "login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, RedirectAttributes attributes){
        User admin = userService.checkUser(username, password);
        if (admin!=null){
            admin.setPassword(null);
            session.setAttribute("user",admin);
            return "admin/index";
        }else {
            attributes.addFlashAttribute("message","用户名或密码错误");
            return "redirect:admin";
        }
    }


    @GetMapping(value = "/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:admin";
    }

}
