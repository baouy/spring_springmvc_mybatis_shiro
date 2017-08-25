package com.py.controller;

import com.py.model.ShiroUser;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pysasuke on 2017/8/21.
 */
@Controller
@RequestMapping("/shiro/user")
public class ShiroUserController {
    private final static Logger log = Logger.getLogger(ShiroUserController.class);

    /**
     * 用户登录
     * @param shiroUser
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public String login(ShiroUser shiroUser, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(shiroUser.getUsername(), shiroUser.getPassword());
        try {
            subject.login(token);//会跳到我们自定义的realm中
            request.getSession().setAttribute("user", shiroUser);
            log.info(shiroUser.getUsername() + "登录");
            return "success";
        } catch (UnknownAccountException e) {
            request.getSession().setAttribute("user", shiroUser);
            return "login";
        } catch (IncorrectCredentialsException e) {
            request.getSession().setAttribute("user", shiroUser);
            request.setAttribute("error", "用户名或密码错误！");
            return "login";
        }
    }

    /**
     * 主页跳转
     * @return
     */
    @RequestMapping("/index")
    public String logout() {
        return "index";
    }

    /**
     * 未授权页跳转
     * @return
     */
    @RequestMapping("/unauthorized")
    public String unauthorized() {
        return "unauthorized";
    }

    /**
     * 登录成功页跳转
     * @return
     */
    @RequestMapping("/person")
    public String admin() {
        return "success";
    }

    @RequestMapping("/student")
    public String student() {
        return "student/success";
    }

    @RequestMapping("/teacher")
    public String teacher() {
        return "teacher/success";
    }
}
