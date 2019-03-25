package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 * Author xushuai
 * Description
 */
public interface WeixinPayService {

    /**
     * 生成本地支付的数据信息
     *
     * @param out_trade_no 外部订单号
     * @param total_fee    金额，单位为：分
     * @return java.util.Map
     */
    Map createNative(String out_trade_no, String total_fee);

    /**
     * 查询订单支付状态
     *
     * @param out_trade_no 外部订单号
     * @return java.util.Map
     */
    Map<String, String> queryPayStatus(String out_trade_no);

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    Map closePay(String out_trade_no);

}
