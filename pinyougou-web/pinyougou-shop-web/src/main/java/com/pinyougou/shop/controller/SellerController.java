package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference(timeout = 10000)
    private SellerService sellerService;

    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller){
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String pwd = encoder.encode(seller.getPassword());
            seller.setPassword(pwd);
            sellerService.save(seller);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /*修改资料（回显数据）*/
    @GetMapping("/sear")
    public Seller search1(String sellerId){
        System.out.println(sellerId);
        Seller seller = sellerService.search1(sellerId);
        return seller;
    }

    /*保存资料（修改资料）*/
    @PostMapping("/save1")
    public boolean save1(@RequestBody Seller seller){
        try {
            System.out.println(seller);
            sellerService.save1(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
