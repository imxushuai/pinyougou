package com.pinyougou.manager.controller;

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
     * 返回当前登录用户名
     *
     * @return java.util.Map
     */
    @RequestMapping("/showName")
    public Map showName() {
        // 使用spring security的方法获取
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        // 封装到 Map 中
        Map<String, String> map = new HashMap<>();
        map.put("loginName", name);

        return map;
    }
}

