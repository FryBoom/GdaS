package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num, Integer ss) {
        try {
            Item item = itemMapper.selectByPrimaryKey(itemId);
            String sellerId = item.getSellerId();
            Cart cart = searchCartBySellerId(carts,sellerId);

            if(cart == null){
                // 创建新的购物车对象
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());
                // 创建订单明细(购物中一个商品)
                OrderItem orderItem = createOrderItem(item,num);
                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.add(orderItem);
                // 为购物车设置订单明细集合
                cart.setOrderItems(orderItems);

                // 将新的购物车对象添加到购物车集合
                carts.add(cart);
            }else {
                // 购物车集合中存在该商家购物车
                // 判断购物车订单明细集合中是否存在该商品
                OrderItem orderItem = searchOrderItemByItemId(cart.getOrderItems(),itemId);
                if(orderItem == null){
                    // 如果没有，新增购物车订单明细
                    orderItem = createOrderItem(item,num);
                    cart.getOrderItems().add(orderItem);
                }else {
                    // 如果有，在原购物车订单明细上添加数量，更改金额
                    if(num == 0){
                        orderItem.setNum(ss);
                    }else {
                        orderItem.setNum(orderItem.getNum() + num);
                    }

                    orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                    // 如果订单明细的购买数小于等于0，则删除
                    if(orderItem.getNum() <= 0){
                        cart.getOrderItems().remove(orderItem);
                    }
                    // 如果cart的orderItems订单明细为0，则删除cart
                    if(cart.getOrderItems().size() == 0){
                        carts.remove(cart);
                    }
                }
            }
            return carts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveCartRedis(String username, List<Cart> carts) {
        redisTemplate.boundValueOps("cart_"+username).set(carts);
    }

    @Override
    public List<Cart> findCartRedis(String username) {
        List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_"+username).get();
        if(carts == null){
            carts = new ArrayList<>();
        }
        return carts;
    }

    @Override
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> carts) {
        for (Cart cart : cookieCarts) {
            for (OrderItem orderItem : cart.getOrderItems()) {
                carts = addItemToCart(carts,orderItem.getItemId(),orderItem.getNum(),0);
            }
        }
        return carts;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        // 创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());

        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems) {
            if(orderItem.getItemId().equals(itemId)){
                return orderItem;
            }
        }
        return null;
    }
}
