package com.pinyougou.page.service;

/**
 * 商品详细页静态化
 * Author xushuai
 * Description
 */
public interface ItemPageService {

    /**
     * 生成商品详细页
     *
     * @param goodsId 商品id
     * @return boolean
     */
    boolean genItemPage(Long goodsId);

    /**
     * 删除指定商品的商品详细页
     *
     * @param goodsIds 商品ID数组
     * @return boolean
     */
    boolean deleteItemPage(Long[] goodsIds);
}

