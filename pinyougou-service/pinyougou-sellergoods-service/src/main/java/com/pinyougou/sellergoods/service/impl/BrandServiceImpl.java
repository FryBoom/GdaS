package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName="com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public void save(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    public void delete(Serializable id) {

    }

    public void deleteAll(Serializable[] ids) {
        //创建示范对象
        Example example = new Example(Brand.class);
        // 创建条件对象
        Example.Criteria criteria = example.createCriteria();
        // 添加in条件
        criteria.andIn("id", Arrays.asList(ids));
        // 根据条件删除
        brandMapper.deleteByExample(example);
    }

    public Brand findOne(Serializable id) {
        return null;
    }

    public List<Brand> findAll() {
        PageHelper.startPage(1,10);
        List<Brand> all = brandMapper.selectAll();
        PageInfo pageInfo = new PageInfo(all);
        return pageInfo.getList();
    }

    public PageResult findByPage(Brand brand, int page, int rows) {
        PageHelper.startPage(page,rows);
        List<Brand> brands = brandMapper.findAll(brand);
        PageInfo pageInfo = new PageInfo(brands);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return brandMapper.findAllByIdAndName();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
