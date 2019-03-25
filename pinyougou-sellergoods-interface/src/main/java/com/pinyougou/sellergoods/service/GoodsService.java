package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbGoods;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface GoodsService {

    /**
     * 返回全部列表
     *
     * @return
     */
    List<TbGoods> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    void add(Goods goods);


    /**
     * 修改
     */
    void update(Goods goods);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    Goods findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    PageResult findPage(TbGoods goods, int pageNum, int pageSize);

    /**
     * 审核
     *
     * @param ids    要审核的商品id
     * @param status 审核后的状态
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 按商品id数组和状态查询商品SKU列表
     *
     * @param ids    商品id数组
     * @param status 状态
     * @return java.util.List<com.pinyougou.pojo.TbItem>
     */
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

}
