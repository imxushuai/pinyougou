package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制层
 * Author xushuai
 * Description
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private OrderService orderService;

    /**
     * 生成二维码数据
     * 
     * @return java.util.Map
     */
    @RequestMapping("/createNative")
    public Map createNative(){
        // 获取当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 从缓存中取出支付日志
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(username);
        if (tbPayLog != null) {
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(), String.valueOf(tbPayLog.getTotalFee()));
        }
        return new HashMap();
    }

    /**
     * 查询订单状态(三秒查询一次，直到支付成功)
     *
     * @param out_trade_no 商家订单号
     * @return entity.Result
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        // 超时标记
        int flag = 0;
        while (true) {
            // 查询订单状态
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = Result.error("支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {
                result = Result.success("支付成功");
                // 支付成功,修改订单状态
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                break;
            }
            try {
                // 三秒查询一次订单状态
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 超时标记
            flag++;
            if (flag >= 100) {// 超时
                // 跳出循环
                result = Result.error("二维码已过期");
                break;
            }
        }
        return result;
    }
}

