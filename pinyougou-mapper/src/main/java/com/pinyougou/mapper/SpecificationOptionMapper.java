package com.pinyougou.mapper;

import com.pinyougou.pojo.Specification;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.SpecificationOption;

import java.util.List;

/**
 * SpecificationOptionMapper 数据访问接口
 * @date 2019-01-11 09:18:45
 * @version 1.0
 */
public interface SpecificationOptionMapper extends Mapper<SpecificationOption>{
    void save(Specification specification);

    List<SpecificationOption> findBySpecId(Long id);

    void deleteBySpecId(Long id);
}