package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关控制层
 * Author xushuai
 * Description
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 获取当前登录用户名
     *
     * @return java.util.Map
     */
    @RequestMapping("/showName")
    public Map showName() {
        Map<String, String> resultMap = new HashMap<>();
        // 获取当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 放入结果集
        resultMap.put("loginName", username);

        return resultMap;
    }
}

