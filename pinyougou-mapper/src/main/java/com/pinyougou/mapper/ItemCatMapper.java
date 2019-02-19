package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.ItemCat;

import java.util.List;

/**
 * ItemCatMapper 数据访问接口
 * @date 2019-01-11 09:18:45
 * @version 1.0
 */
public interface ItemCatMapper extends Mapper<ItemCat>{


    void deleteById(List<Long> list);
}