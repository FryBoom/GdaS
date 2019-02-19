package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.SpecificationService")
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;
    @Override
    public void save(Specification specification) {
        try {
            specificationMapper.insertSelective(specification);
            specificationOptionMapper.save(specification);
        }catch (Exception e){
            throw new  RuntimeException(e);
        }

    }

    @Override
    public void update(Specification specification) {
        specificationMapper.updateByPrimaryKey(specification);
        specificationOptionMapper.deleteBySpecId(specification.getId());

        if(specification.getSpecificationOptions().size() > 0
                && specification.getSpecificationOptions().get(0).getOptionName()!=""
                && specification.getSpecificationOptions().get(0).getOptionName()!=null) {
            specificationOptionMapper.save(specification);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        //创建示范对象
        Example example = new Example(Specification.class);
        // 创建条件对象
        Example.Criteria criteria = example.createCriteria();
        // 添加in条件
        criteria.andIn("id", Arrays.asList(ids));
        // 根据条件删除
        specificationMapper.deleteByExample(example);

        /*==========================*/
        Example example1 = new Example(SpecificationOption.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("specId",Arrays.asList(ids));
        specificationOptionMapper.deleteByExample(example1);
    }

    @Override
    public Specification findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Specification> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Specification specification, int page, int rows) {
        try {
            PageHelper.startPage(page,rows);
            List<Specification> all = specificationMapper.findAll(specification);
            PageInfo pageInfo = new PageInfo(all);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpecificationOption> findBySpecId(Long id) {
        return specificationOptionMapper.findBySpecId(id);
    }

    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return specificationMapper.findAllByIdAndName();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
