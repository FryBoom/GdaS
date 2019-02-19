package com.pinyougou.mapper;

import com.pinyougou.pojo.TypeTemplate;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * TypeTemplateMapper 数据访问接口
 * @date 2019-01-11 09:18:45
 * @version 1.0
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate>{
    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    @Select("select id,name from tb_type_template order by id asc")
    List<Map<String,Object>> findTypeTemplateList();
}