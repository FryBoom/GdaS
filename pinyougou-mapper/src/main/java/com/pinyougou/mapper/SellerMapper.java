package com.pinyougou.mapper;

import com.pinyougou.pojo.Seller;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 * @date 2019-01-11 09:18:45
 * @version 1.0
 */
public interface SellerMapper extends Mapper<Seller>{

    List<Seller> findAll(Seller seller);

    Seller find(String sellerId);
}