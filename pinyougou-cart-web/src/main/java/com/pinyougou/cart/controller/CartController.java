package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车控制层
 * Author xushuai
 * Description
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    /** cookieName:cookie名称 */
    private final String CARTLIST_COOKIENAME = "cartList";
    /** cookie_maxAge:cookie存活时间 */
    private final int MAXAGE_COOKIE = 216000;
    /** 匿名角色名称 */
    private final String ROLE_ANONYMOUSUSER = "anonymousUser";

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    /**
     * 添加商品到购物车
     *
     * @param itemId 商品ID
	 * @param num 数量
     * @return entity.Result
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        // 获取当前登录用户名
        String loginUser = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<Cart> cartList = findCartList();
            // 添加商品到购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            // 判断是否存入redis
            if (loginUser.equals(ROLE_ANONYMOUSUSER)) {// 未登录
                // 将购物车列表转换为json字符串
                String cookieValue = JSON.toJSONString(cartList);
                // 将购物车添加到cookie中
                CookieUtil.setCookie(request, response, CARTLIST_COOKIENAME, cookieValue, MAXAGE_COOKIE, "UTF-8");
            } else {// 已登录
                // 存入redis
                cartService.saveCartListToRedis(loginUser, cartList);
            }

            return Result.success("添加商品到购物车成功");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("添加商品到购物车失败");
        }
    }

    /**
     * 获取购物车列表
     *
     * @return java.util.List<com.pinyougou.pojogroup.Cart>
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        // 获取当前登录用户名
        String loginUser = SecurityContextHolder.getContext().getAuthentication().getName();
        // 从cookie中获取购物车列表
        String cartListJson = CookieUtil.getCookieValue(request, CARTLIST_COOKIENAME, "UTF-8");
        if (cartListJson == null || cartListJson.equals("")) {
            cartListJson = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListJson, Cart.class);
        // 判断是否为匿名权限
        if (loginUser.equals(ROLE_ANONYMOUSUSER)) {// 为匿名权限，未登录状态

            return cartList_cookie;
        } else {// 不是匿名权限，登录状态
            // 从redis中获取购物车列表
            List<Cart> cartList_redis = cartService.findCartListFromRedis(loginUser);
            // cookie购物车中存在数据
            if (cartList_cookie.size() > 0) {
                // 进行购物车合并
                cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
                // 清除cookie中的购物车数据
                CookieUtil.deleteCookie(request, response, CARTLIST_COOKIENAME);
                // 将合并后的购物车数据存入reids
                cartService.saveCartListToRedis(loginUser, cartList_redis);
            }

            return cartList_redis;
        }
    }


}
