package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentCategoryMapper;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.Content;
import com.pinyougou.pojo.ContentCategory;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ContentService")
@Transactional
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(Content content) {
        try {
            contentMapper.insert(content);
            redisTemplate.delete("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Content content) {
        try {
            contentMapper.updateByPrimaryKey(content);
            redisTemplate.delete("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            Example example = new Example(Content.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", Arrays.asList(ids));
            contentMapper.deleteByExample(example);
            redisTemplate.delete("content");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Content findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Content> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Content content, int page, int rows) {
        try {
            PageHelper.startPage(page,rows);
            List<Content> all = contentMapper.selectAll();
            PageInfo pageInfo = new PageInfo(all);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ContentCategory> findContentCategory() {
        try {
            return contentCategoryMapper.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Content> findContentById(Long categoryId) {
        List<Content> contentList = null;

        try {
            /** 从Redis中获取广告 */
            contentList = (List<Content>) redisTemplate.boundValueOps("content").get();
            if(contentList != null && contentList.size() > 0){
                return contentList;
            }
        } catch (Exception e) {}
            try {
                contentList = contentMapper.findContentBycategoryId(categoryId);
                try {
                    redisTemplate.boundValueOps("content").set(contentList);
                } catch (Exception e) {}
                return contentList;
            }catch (Exception e){
                throw new RuntimeException(e);
            }

    }
}
