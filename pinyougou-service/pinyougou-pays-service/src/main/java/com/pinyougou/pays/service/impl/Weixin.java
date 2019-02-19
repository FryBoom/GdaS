package com.pinyougou.pays.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.WeixinPayService")
@Transactional
public class Weixin implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${unifiedorder}")
    private String unifiedorder;
    @Value("${orderquery}")
    private String orderquery;
    @Value("${closeorder}")
    private String closeorder;

    @Override
    public Map<String, String> genPayCode(String outTradeNo, String totalFee) {
        /** 创建Map集合封装请求参数 */
        Map<String, String> param = new HashMap<>();
        /** 公众号 */
        param.put("appid", appid);
        /** 商户号 */
        param.put("mch_id", partner);
        /** 随机字符串 */
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        /** 商品描述 */
        param.put("body", "品优购");
        /** 商户订单交易号 */
        param.put("out_trade_no", outTradeNo);
        /** 总金额（分） */
        param.put("total_fee",totalFee);
        /** IP地址 */
        param.put("spbill_create_ip", "127.0.0.1");
        /** 回调地址(随意写) */
        param.put("notify_url", "http://test.itcast.cn");
        /** 交易类型 */
        param.put("trade_type", "NATIVE");

        try {
            /** 根据商户密钥签名生成XML格式请求参数 */
            String xmlParam = WXPayUtil.generateSignedXml(param,partnerkey);
            /** 创建HttpClientUtils对象发送请求 */
            HttpClientUtils client = new HttpClientUtils(true);
            /** 发送请求，得到响应数据 */
            String result = client.sendPost(unifiedorder, xmlParam);
            Map<String,String> resultMap = WXPayUtil.xmlToMap(result);
            /** 创建Map集合封装返回数据 */
            Map<String,String> data = new HashMap<>();
            data.put("codeUrl",resultMap.get("code_url"));
            data.put("totalFee",totalFee);
            data.put("outTradeNo",outTradeNo);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        /** 创建Map集合封装请求参数 */
        Map<String, String> param = new HashMap<>(5);
        /** 公众号 */
        param.put("appid", appid);
        /** 商户号 */
        param.put("mch_id", partner);
        /** 订单交易号 */
        param.put("out_trade_no", outTradeNo);
        /** 随机字符串 */
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            /** 根据商户密钥签名生成XML格式请求参数 */
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数：" + xmlParam);
            /** 创建HttpClientUtils对象发送请求 */
            HttpClientUtils client = new HttpClientUtils(true);
            /** 发送请求，得到响应数据 */
            String result = client.sendPost(orderquery, xmlParam);
            System.out.println("响应数据：" + result);
            /** 将响应数据XML格式转化成Map集合 */
            return WXPayUtil.xmlToMap(result);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Map<String, String> closePayTimeout(String outTradeNo) {
        /* 创建Map集合封装请求参数 */
        Map<String,String> params = new HashMap<>();
        /** 公众账号 */
        params.put("appid",appid);
        /** 商户账号 */
        params.put("mch_id",partner);
        /** 订单交易号 */
        params.put("out_trade_no",outTradeNo);
        /** 随机字符串 */
        params.put("nonce_str",WXPayUtil.generateNonceStr());

        try {
            /** 生成签名的xml参数 */
            String xmlParam = WXPayUtil.generateSignedXml(params,partnerkey);
            /** 创建HttpClientUtils对象 */
            HttpClientUtils client = new HttpClientUtils(true);
            /** 发送post请求，得到响应数据 */
            String result = client.sendPost(closeorder,xmlParam);
            /** 将xml响应数据转化成Map */
            return WXPayUtil.xmlToMap(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
