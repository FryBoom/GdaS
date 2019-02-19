package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;
    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        typeTemplateMapper.deleteByExample(example);
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try {
            PageHelper.startPage(page,rows);
            List<TypeTemplate> all = typeTemplateMapper.findAll(typeTemplate);
            PageInfo pageInfo = new PageInfo(all);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Map<String, Object>> findTypeTemplateList() {
        try {
            return typeTemplateMapper.findTypeTemplateList();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map> findSpecByTemplateId(Long id) {
        try {
            TypeTemplate typeTemplate = this.findOne(id);
            /**
             * [{"id":33,"text":"电视屏幕尺寸"}]
             * 获取模版中所有的规格，转化成  List<Map>
             */
            List<Map> specLists = JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
            for (Map map:specLists){
                SpecificationOption so = new SpecificationOption();
                so.setSpecId(Long.valueOf(map.get("id").toString()));
                List<SpecificationOption> specificationOptions =
                        specificationOptionMapper.select(so);
                map.put("options",specificationOptions);
            }
            return specLists;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
