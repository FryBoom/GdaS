package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ItemCatService")
@Transactional
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Override
    public void save(ItemCat itemCat) {

    }

    @Override
    public void update(ItemCat itemCat) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public ItemCat findOne(Serializable id) {
        return null;
    }

    @Override
    public List<ItemCat> findAll() {
        return null;
    }

    @Override
    public List<ItemCat> findByPage(ItemCat itemCat, int page, int rows) {
        return null;
    }

    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {

        try {
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            return itemCatMapper.select(itemCat);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageResult findItemCatByParentId(Integer page,Integer rows, Long parentId) {
        try {
            PageHelper.startPage(page,rows);

            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            List<ItemCat> select = itemCatMapper.select(itemCat);

            PageInfo pageInfo = new PageInfo(select);
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveItemCat(ItemCat itemCat) {
        try {
            itemCatMapper.insertSelective(itemCat);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateItemCat(ItemCat itemCat) {
        try{
            itemCatMapper.updateByPrimaryKeySelective(itemCat);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteItemCat(Long[] ids) {
        try {
            List<Long> list = new ArrayList<>();
            for (Long id : ids) {
                list.add(id);
                findLeafNode(id,list);
            }
            itemCatMapper.deleteById(list);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void findLeafNode(Long id, List<Long> idLists){
        List<ItemCat> itemCats = findItemCatByParentId(id);

        if(itemCats != null && itemCats.size() > 0){
            for (ItemCat itemCat : itemCats) {
                idLists.add(itemCat.getId());
                findLeafNode(itemCat.getId(),idLists);
            }
        }
    }

}
