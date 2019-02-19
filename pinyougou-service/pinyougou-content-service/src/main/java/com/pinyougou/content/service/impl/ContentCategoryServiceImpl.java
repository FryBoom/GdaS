package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentCategoryMapper;
import com.pinyougou.pojo.ContentCategory;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ContentCategoryService")
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;
    @Override
    public void save(ContentCategory contentCategory) {
        try {
            contentCategoryMapper.insert(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ContentCategory contentCategory) {
        try {
            contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        Example example = new Example(ContentCategory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        contentCategoryMapper.deleteByExample(example);
    }

    @Override
    public ContentCategory findOne(Serializable id) {
        return null;
    }

    @Override
    public List<ContentCategory> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(ContentCategory contentCategory, int page, int rows) {
        try {
            PageHelper.startPage(page,rows);
            List<ContentCategory> all = contentCategoryMapper.selectAll();
            PageInfo pageInfo = new PageInfo(all);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
