package com.pinyougou.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name="tb_goods_desc")
public class GoodsDesc implements Serializable{
	private static final long serialVersionUID = -7405298140497362200L;
	@Id @Column(name="goods_id")
	private Long goodsId;
	@Column(name="introduction")
    private String introduction;
	@Column(name="specification_items")
    private String specificationItems;
	@Column(name="custom_attribute_items")
    private String customAttributeItems;
	@Column(name="item_images")
    private String itemImages;
	/*包装列表*/
	@Column(name="package_list")
    private String packageList;
	/*售后服务*/
	@Column(name="sale_service")
    private String saleService;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

    public String getSpecificationItems() {
        return specificationItems;
    }

    public void setSpecificationItems(String specificationItems) {
        this.specificationItems = specificationItems == null ? null : specificationItems.trim();
    }

    public String getCustomAttributeItems() {
        return customAttributeItems;
    }

    public void setCustomAttributeItems(String customAttributeItems) {
        this.customAttributeItems = customAttributeItems == null ? null : customAttributeItems.trim();
    }

    public String getItemImages() {
        return itemImages;
    }

	public void setItemImages(String itemImages) {
		this.itemImages = itemImages;
	}

    public String getPackageList() {
        return packageList;
    }

    public void setPackageList(String packageList) {
        this.packageList = packageList == null ? null : packageList.trim();
    }

    public String getSaleService() {
        return saleService;
    }

    public void setSaleService(String saleService) {
        this.saleService = saleService == null ? null : saleService.trim();
    }
}