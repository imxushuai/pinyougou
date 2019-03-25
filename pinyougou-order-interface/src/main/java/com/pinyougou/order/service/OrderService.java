package com.pinyougou.order.service;

import java.util.List;

import com.pinyougou.pojo.TbOrder;

import com.pinyougou.pojo.TbPayLog;
import entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface OrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    List<TbOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    void add(TbOrder order);


    /**
     * 修改
     */
    void update(TbOrder order);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    TbOrder findOne(Long id);


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
    PageResult findPage(TbOrder order, int pageNum, int pageSize);

    /**
     * 获取指定用户的支付日志
     *
     * @param username 用户登录名
     * @return com.pinyougou.pojo.TbPayLog
     */
    TbPayLog searchPayLogFromRedis(String username);

    /**
     * 修改订单状态
     *
     * @param out_trade_no   支付订单号
     * @param transaction_id 微信返回的交易流水号
     */
    void updateOrderStatus(String out_trade_no, String transaction_id);

}
