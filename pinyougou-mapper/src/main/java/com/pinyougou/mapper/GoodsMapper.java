package com.pinyougou.mapper;

import com.pinyougou.pojo.Goods;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 * @date 2019-01-11 09:18:45
 * @version 1.0
 */
public interface GoodsMapper extends Mapper<Goods>{


    List<Map<String,Object>> findAll(Goods goods);

    void updateStatus(@Param("ids")Long[] ids,@Param("status")String status);

    void updateDeleteStatus(@Param("ids")Serializable[] ids,@Param("isDelete") String s);

    void updateMarketable(@Param("ids")Long[] ids, @Param("status")String status);
}