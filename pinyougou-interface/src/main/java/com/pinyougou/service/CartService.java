package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addItemToCart(List<Cart> carts,
                             Long itemId, Integer num, Integer ss);

    void saveCartRedis(String username, List<Cart> carts);

    List<Cart> findCartRedis(String username);

    List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> carts);
}
