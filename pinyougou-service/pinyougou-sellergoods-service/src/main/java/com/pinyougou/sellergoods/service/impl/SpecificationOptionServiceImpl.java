package com.pinyougou.sellergoods.service.impl;

import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationOptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

public class SpecificationOptionServiceImpl implements SpecificationOptionService{
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(SpecificationOption specificationOption) {

    }

    @Override
    public void update(SpecificationOption specificationOption) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SpecificationOption findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SpecificationOption> findAll() {
        return null;
    }

    @Override
    public List<SpecificationOption> findByPage(SpecificationOption specificationOption, int page, int rows) {
        return null;
    }
}
