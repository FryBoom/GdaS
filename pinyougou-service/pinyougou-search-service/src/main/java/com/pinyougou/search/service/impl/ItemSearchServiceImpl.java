package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        Map<String,Object> data = new HashMap<>();

        /** 获取检索关键字 */
        String keywords = (String) params.get("keywords");

        /*获取当前页码*/
        Integer page = (Integer) params.get("page");
        if(page == null) page = 1;
        Integer rows = (Integer) params.get("rows");
        if(rows == null) rows = 15;
        /** 判断检索关键字 */
        if(StringUtils.isNoneBlank(keywords)){
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            HighlightOptions highlightOptions = new HighlightOptions();
            highlightOptions.addField("title");
            highlightOptions.setSimplePrefix("<font color='red'>");
            highlightOptions.setSimplePostfix("</font>");
            highlightQuery.setHighlightOptions(highlightOptions);

            /** 创建查询条件 */
            Criteria criteria = new Criteria("keywords").is(keywords);
            /** 添加查询条件 */
            highlightQuery.addCriteria(criteria);

            /** 按商品分类过滤 */
            if(!"".equals(params.get("category"))){
                Criteria criteria1 = new Criteria("category").is(params.get("category"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            /** 按品牌过滤 */
            if(!"".equals(params.get("brand"))){
                Criteria criteria1 = new Criteria("brand").is(params.get("brand"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            /** 按规格过滤 */
            if(params.get("spec") != null){
                Map<String,String> specMap = (Map) params.get("spec");
                for (String key : specMap.keySet()) {
                    Criteria criteria1 = new Criteria("spec_"+key).is(specMap.get(key));
                    /** 添加过滤条件 */
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }
            /** 按价格过滤 */
            if(!"".equals(params.get("price"))){
                String[] price = params.get("price").toString().split("-");
                if (!price[0].equals("0")){
                    Criteria criteria1 = new Criteria("price").greaterThanEqual(price[0]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                /** 如果价格区间终点不等于星号 */
                if(!price[1].equals("*")){
                    Criteria criteria1 = new Criteria("price").lessThanEqual(price[1]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }
            /** 添加排序 */
            String sortValue = (String) params.get("sort");
            String sortField = (String) params.get("sortField");
            if(StringUtils.isNoneBlank(sortValue) && StringUtils.isNoneBlank(sortField)){
                Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue) ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sortField);
                highlightQuery.addSort(sort);
            }
            /** 设置起始记录查询数 */
            highlightQuery.setOffset((page - 1) * rows);
            /** 设置每页显示记录数 */
            highlightQuery.setRows(rows);

            /** 分页查询，得到高亮分页查询对象 */
            HighlightPage<SolrItem> highlightPage =
                    solrTemplate.queryForHighlightPage(highlightQuery,SolrItem.class);
            /** 循环高亮项集合 */
            for (HighlightEntry<SolrItem> he : highlightPage.getHighlighted()) {
                SolrItem solrItem = he.getEntity();
                /** 判断高亮集合及集合中第一个Field的高亮内容 */
                if(he.getHighlights().size()>0 && he.getHighlights().get(0).getSnipplets().size()>0){
                    solrItem.setTitle(he.getHighlights().get(0).getSnipplets().get(0));
                }
            }
            data.put("rows",highlightPage.getContent());

            /** 设置总页数 */
            data.put("totalPages",highlightPage.getTotalPages());
            /*设置总记录数*/
            data.put("total",highlightPage.getTotalElements());
        }else {
            SimpleQuery simpleQuery = new SimpleQuery("*:*");
            /** 设置起始记录查询数 */
            simpleQuery.setOffset((page -1) *rows);
            /** 设置每页显示记录数 */
            simpleQuery.setRows(rows);
            /** 分页检索 */
            ScoredPage<SolrItem> scoredPage
                    = solrTemplate.queryForPage(simpleQuery,SolrItem.class);
            data.put("rows",scoredPage.getContent());
            /** 设置总页数 */
            data.put("totalPages",scoredPage.getTotalPages());
            /*设置总记录数*/
            data.put("total",scoredPage.getTotalElements());
        }


        return data;
    }

    @Override
    public void saveOrUpdate(List<SolrItem> solrItems) {
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if(updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }

    @Override
    public void delete(List<Long> goodsIds) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("goodsId").in(goodsIds);
        query.addCriteria(criteria);
        UpdateResponse updateResponse = solrTemplate.delete(query);
        if(updateResponse.getStatus() == 0){
            solrTemplate.commit();
        }else {
            solrTemplate.rollback();
        }
    }
}
