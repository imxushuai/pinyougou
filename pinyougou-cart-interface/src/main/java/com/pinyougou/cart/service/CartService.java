package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车接口
 * Author xushuai
 * Description
 */
public interface CartService {

    /**
     * 添加商品到购物车中
     *
     * @param cartList 购物车列表
     * @param itemId 商品ID
	 * @param num 添加的数量
     * @return java.util.List<com.pinyougou.pojogroup.Cart>
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 从redis中获取购物车列表
     *
     * @param
     * @return java.util.List<com.pinyougou.pojogroup.Cart>
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车列表保存到redis中
     *
     * @param username 当前登录用户名
     * @param cartList 购物车列表
     */
    void saveCartListToRedis(String username, List<Cart> cartList);

    /**
     * 合并购物车
     *
     * @param cartList1 购物车1
	 * @param cartList2 购物车2
     * @return java.util.List<com.pinyougou.pojogroup.Cart>
     */
    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}

