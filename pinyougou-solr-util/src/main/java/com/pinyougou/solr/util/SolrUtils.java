package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /** 带条件分页查询 */
    public void findByPage() {
       /* *//** 创建查询对象 *//*
        Query query = new SimpleQuery("*:*");
        *//** 创建条件对象(标题包含2) *//*
        Criteria criteria = new Criteria("title").contains("2");
        *//** 添加条件 *//*
        query.addCriteria(criteria);
        *//** 设置分页开始记录数(第一页) 默认0 *//*
        query.setOffset(0);
        *//** 设置每页显示记录数，默认10 *//*
        query.setRows(20);
        ScoredPage<Item> page = solrTemplate.queryForPage(query,Item.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<Item> items = page.getContent();
        for (Item item : items) {
            System.out.println(item.getTitle() + "\t" + item.getPrice());
        }*/
    }

    /** 根据主键id查询 */
    public void findOne(){
        Item item = solrTemplate.getById(3, Item.class);
        System.out.println(item.getTitle());
    }

    /** 添加或修改 */
    public void saveOrUpdate(){
        Item item = new Item();
        item.setId(3L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(1L);
        item.setSeller("华为2号专卖店");
        item.setTitle("华为Mate9");
        item.setPrice(new BigDecimal(2000));
        item.setUpdateTime(new Date());
        /** 添加或修改 */
        UpdateResponse updateResponse = solrTemplate.saveBean(item);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }

    /** 根据主键id删除 */
    public void deleteById(){
        UpdateResponse updateResponse = solrTemplate.deleteById("3");
        if(updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }

    public void importItemData(){
        Item item = new Item();
        item.setStatus("1");
        List<Item> list = itemMapper.select(item);
        List<SolrItem> solrItems = new ArrayList<>();
        for (Item item1 : list) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setBrand(item1.getBrand());
            solrItem.setCategory(item1.getCategory());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setImage(item1.getImage());
            solrItem.setPrice(item1.getPrice());
            solrItem.setSeller(item1.getSeller());
            solrItem.setTitle(item1.getTitle());
            solrItem.setUpdateTime(item1.getUpdateTime());
            /** 将spec字段的json字符串转换成map */
            Map specMap = JSON.parseObject(item1.getSpec(),Map.class);
            /** 设置动态域 */
            solrItem.setSpecMap(specMap);
            solrItems.add(solrItem);
        }

        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);

        if(updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }

    }

    public void deleteAll(){
        Query query = new SimpleQuery("*:*");
        UpdateResponse updateResponse = solrTemplate.delete(query);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = new
                ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SolrUtils solrUtils = context.getBean(SolrUtils.class);
        solrUtils.importItemData();
    }
}
