package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import exception.PinyougouException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车实现
 * Author xushuai
 * Description
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    
    /** redis中购物车的key */
    private final String REDIS_CARTLIST_KEY = "cartList";


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        /*
         * 1.使用SKUID查询SKU商品对象
         * 2.使用商品对象获取商家信息
         * 3.根据商家ID在购物车列表中查询购物车对象
         * 4.购物车列表中不存在该商家的购物车
         *      4.1 创建新的购物车对象，将新的购物车对象添加到购物车列表
         * 5.购物车列表中存在该商家的购物车
         *      5.1 判断该购物车是否存在该商品的明细
         *          5.1.1 不存在，新增该明细到明细列表
         *          5.1.2 存在，在原有的数量上加上新增的数量，并更改金额
         */
        // 1.查询SKU商品对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new PinyougouException("商品不存在!");
        }
        if (!item.getStatus().equals(TbItem.STATUS_NORMAL)) {
            throw new PinyougouException("商品状态异常");
        }
        // 2.获取商家信息
        String sellerId = item.getSellerId();
        String sellerName = item.getSeller();
        // 3.根据商家ID查询购物车中的对象
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if (cart == null) {// 4.购物车列表中不存在该商家的购物车
            // 创建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(sellerName);
            List<TbOrderItem> orderItemList = new ArrayList<>();
            // 使用item对象生成购物车明细
            TbOrderItem tbOrderItem = itemToOrderItem(item, num);
            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);

            // 将购物车放入购物车列表
            cartList.add(cart);

        } else {// 5.购物车列表中存在该商家的购物车
            // 5.1 判断该购物车是否存在该商品的明细
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), item.getId());
            if (orderItem == null) {// 5.1.1 不存在，新增该明细到明细列表
                orderItem = itemToOrderItem(item, num);
                // 添加到明细列表
                cart.getOrderItemList().add(orderItem);
            } else {// 5.1.2 存在，在原有的数量上加上新增的数量，并更改金额
                // 判断操作后的购物车情况
                if (orderItem.getNum() + num < 1) {// 数量小于1
                    // 移除该明细
                    cart.getOrderItemList().remove(orderItem);
                    if (cart.getOrderItemList().size() == 0) {// 明细列表中无数据
                        // 移除该购物车
                        cartList.remove(cart);
                    }
                }
                // 修改数量
                orderItem.setNum(orderItem.getNum() + num);
                // 修改金额
                orderItem.setTotalFee(BigDecimal.valueOf(orderItem.getPrice().doubleValue() * orderItem.getNum()));
            }
        }

        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中获取当前用户的购物车");
        // 从购物车中获取
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CARTLIST_KEY).get(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("将购物车存入redis中");
        // 将购物车保存到redis
        redisTemplate.boundHashOps(REDIS_CARTLIST_KEY).put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        // 遍历任意购物车
        for (Cart cart : cartList1) {
            // 遍历购物车明细列表
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                // 进行合并操作
                cartList2 = addGoodsToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     * 根据itemId查询购物车明细列表
     *
     * @param orderItemList 购物车明细列表
     * @param itemId        商品ID
     * @return com.pinyougou.pojo.TbOrderItem
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        // 遍历购物车明细列表
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 将item对象转换为OrderItem对象
     *
     * @param item 商品对象
     * @param num  数量
     * @return com.pinyougou.pojo.TbOrderItem
     */
    private TbOrderItem itemToOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(BigDecimal.valueOf(item.getPrice().doubleValue() * num));

        return orderItem;
    }

    /**
     * 根据商家ID查询购物车列表
     *
     * @param cartList 购物车列表
     * @param sellerId 商家ID
     * @return com.pinyougou.pojogroup.Cart
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        // 遍历购物车列表
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }


}

