package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 30000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @GetMapping("/addCart")
    @CrossOrigin(origins = "http://item.pinyougou.com",allowCredentials = "true")
    public boolean addCart(Long itemId,Integer num,Integer s){
        try {
            // 获取登录用户名
            String username = request.getRemoteUser();
            List<Cart> carts = findCart();
            carts = cartService.addItemToCart(carts,itemId,num,s);
            if(StringUtils.isNoneBlank(username)){
                cartService.saveCartRedis(username,carts);
            }else {
                CookieUtils.setCookie(request,response,CookieUtils.CookieName.PINYOUGOU_CART,
                        JSON.toJSONString(carts),3600*24,true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/findCart")
    public List<Cart> findCart(){
        String username = request.getRemoteUser();
        List<Cart> carts = null;
        // 判断是否为空
        if(StringUtils.isNoneBlank(username)){
            // 已登录
            /**######## 从Redis获取购物车 #######*/
            carts = cartService.findCartRedis(username);
            // 从Cookie中获取购物车集合json字符串
            String cartStr = CookieUtils.getCookieValue
                    (request,CookieUtils.CookieName.PINYOUGOU_CART, true);
            // 判断是否为空
            if(StringUtils.isNoneBlank(cartStr)){
                // 转化成List集合
                List<Cart> cookieCarts = JSON.parseArray(cartStr,Cart.class);
                if(cookieCarts != null && cookieCarts.size() > 0){
                    // 合并购物车
                    carts = cartService.mergeCart(cookieCarts,carts);
                    // 将合并后的购物车存入Redis
                    cartService.saveCartRedis(username,carts);
                    // 删除Cookie购物车
                    CookieUtils.deleteCookie(request,response,CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }
        }else {
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            // 判断是否为空
            if (StringUtils.isBlank(cartStr)) {
                cartStr = "[]";
            }
            carts = JSON.parseArray(cartStr, Cart.class);
        }
        return carts;
    }
}
