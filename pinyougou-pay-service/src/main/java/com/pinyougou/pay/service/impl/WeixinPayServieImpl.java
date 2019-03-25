package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付接口实现
 * Author xushuai
 * Description
 */
@Service
public class WeixinPayServieImpl implements WeixinPayService {

    @Value("${appid}")
    private String WEIXIN_APPID;
    @Value("${partner}")
    private String WEIXIN_PARTNER;
    @Value("${partnerkey}")
    private String WEIXIN_PARTNERKEY;
    @Value("${notifyurl}")
    private String WEIXIN_NOTIFYURL;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            // 1.封装微信支付请求参数
            Map<String, String> param = buildPayParam(out_trade_no, total_fee);

            // 2.使用请求参数生成请求xml数据
            String signXml = WXPayUtil.generateSignedXml(param, WEIXIN_PARTNERKEY);
            System.out.println("xml数据：" + signXml);

            // 3.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signXml);
            httpClient.post();

            // 4.获取请求响应结果
            String response = httpClient.getContent();
            Map<String, String> responseMap = WXPayUtil.xmlToMap(response);

            System.out.println("响应结果：" + responseMap);
            // 5.生成返回结果
            if (responseMap.get("return_code").equals("SUCCESS")) {
                Map resultMap = new HashMap();
                resultMap.put("code_url", responseMap.get("code_url"));// 二维码链接地址
                resultMap.put("out_trade_no", out_trade_no);// 订单号
                resultMap.put("total_fee", total_fee);// 金额

                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        try {
            // 1.封装请求参数
            Map<String, String> param = buildQueryPayStatusParam(out_trade_no);

            // 2.使用请求参数生成请求xml数据
            String signXml = WXPayUtil.generateSignedXml(param, WEIXIN_PARTNERKEY);
            System.out.println("xml数据：" + signXml);

            // 3.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signXml);
            httpClient.post();

            // 4.获取请求结果
            String response = httpClient.getContent();
            Map<String, String> responseMap = WXPayUtil.xmlToMap(response);
            System.out.println("响应结果：" + response);

            return responseMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", WEIXIN_APPID);//公众账号ID
        param.put("mch_id", WEIXIN_PARTNER);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, WEIXIN_PARTNERKEY);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 构造查询支付状态的请求参数
     *
     * @param out_trade_no 商户订单号
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    private Map<String, String> buildQueryPayStatusParam(String out_trade_no) {
        Map<String, String> param = new HashMap<>();
        // 公众账号ID
        param.put("appid", WEIXIN_APPID);
        // 商户号
        param.put("mch_id", WEIXIN_PARTNER);
        // 商户订单号
        param.put("out_trade_no", out_trade_no);
        // 随机字符串
        param.put("nonce_str", WXPayUtil.generateNonceStr());

        return param;
    }

    /**
     * 构造微信支付请求参数
     *
     * @param out_trade_no 外部订单号
     * @param total_fee    金额
     */
    private Map<String, String> buildPayParam(String out_trade_no, String total_fee) {
        Map<String, String> param = new HashMap<>();
        // 公众账号ID
        param.put("appid", WEIXIN_APPID);
        // 商户号
        param.put("mch_id", WEIXIN_PARTNER);
        // 设备号(自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB")
        //param.put("device_info", "WEB");
        // 随机字符串(随机字符串，长度要求在32位以内。推荐随机数生成算法)
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        // 商品描述
        param.put("body", "品优购");
        // 商品详情
        //param.put("detail", WEIXIN_APPID);
        // 商户订单号
        param.put("out_trade_no", out_trade_no);
        // 标价金额
        param.put("total_fee", total_fee);
        // 终端IP
        param.put("spbill_create_ip", "127.0.0.1");
        // 通知地址
        param.put("notify_url", "http://test.itcast.cn");
        // 交易类型
        param.put("trade_type", "NATIVE");

        return param;
    }
}

