package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务层接口
 * Author xushuai
 * Description
 */
public interface ItemSearchService {

    /**
     * 搜索
     *
     * @param searchMap 搜索条件
     * @return java.util.Map
     */
    Map search(Map searchMap);

    /**
     * 将商品SKU数据导入索引库
     *
     * @param itemList 商品SKU列表
     */
    void importList(List<TbItem> itemList);

    /**
     * 按商品id集合删除索引库数据
     *
     * @param ids 商品id集合
     */
    void deleteByGoodsIds(List ids);

}

