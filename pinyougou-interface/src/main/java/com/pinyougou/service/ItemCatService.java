package com.pinyougou.service;

import com.pinyougou.pojo.ItemCat;
import com.pinyougou.pojo.PageResult;

import java.util.List;
import java.io.Serializable;
/**
 * ItemCatService 服务接口
 * @date 2019-01-11 09:25:16
 * @version 1.0
 */
public interface ItemCatService {

	/** 添加方法 */
	void save(ItemCat itemCat);

	/** 修改方法 */
	void update(ItemCat itemCat);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	ItemCat findOne(Serializable id);

	/** 查询全部 */
	List<ItemCat> findAll();

	/** 多条件分页查询 */
	List<ItemCat> findByPage(ItemCat itemCat, int page, int rows);

    List<ItemCat> findItemCatByParentId(Long parentId);

    PageResult findItemCatByParentId(Integer page,Integer rows, Long parentId);

	void saveItemCat(ItemCat itemCat);

    void updateItemCat(ItemCat itemCat);

	void deleteItemCat(Long[] ids);
}