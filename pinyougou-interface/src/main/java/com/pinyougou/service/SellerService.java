package com.pinyougou.service;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Seller;

import java.io.Serializable;
import java.util.List;
/**
 * SellerService 服务接口
 * @date 2019-01-11 09:25:16
 * @version 1.0
 */
public interface SellerService {

	/** 添加方法 */
	void save(Seller seller);

	/** 修改方法 */
	void update(Seller seller);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Seller findOne(Serializable id);

	/** 查询全部 */
	List<Seller> findAll();

	/** 多条件分页查询 */
	PageResult findByPage(Seller seller, int page, int rows);

    void updateStatus(String sellerId, String status);

    /*根据商家登陆用户名查询商家信息*/
    Seller search1(String sellerId);

	/*保存资料*/
	void save1(Seller seller);
}