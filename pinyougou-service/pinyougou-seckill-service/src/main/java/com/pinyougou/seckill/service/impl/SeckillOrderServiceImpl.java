package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    @Override
    public void submitOrderToRedis(Long id, String userId) {
        try {
            // 从Redis中查询秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
            // 判断库存数据
            if(seckillGoods != null && seckillGoods.getStockCount() >0){
                // 减库存(redis)
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                // 判断是否已经被秒光
                if(seckillGoods.getStockCount() == 0){
                    // 同步秒杀商品到数据库(修改库存)
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    // 删除Redis中的秒杀商品
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                } else {
                    // 重新存入Redis中
                    redisTemplate.boundHashOps("seckillGoodsList").put(id,seckillGoods);
                }
                // 创建秒杀订单对象
                SeckillOrder seckillOrder = new SeckillOrder();
                // 设置订单id
                seckillOrder.setId(idWorker.nextId());
                // 设置秒杀商品id
                seckillOrder.setSeckillId(id);
                // 设置秒杀价格
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 设置用户id
                seckillOrder.setUserId(userId);
                // 设置商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 设置创建时间
                seckillOrder.setCreateTime(new Date());
                // 设置状态码(未付款)
                seckillOrder.setStatus("0");
                // 保存订单到Redis
                redisTemplate.boundHashOps("seckillOrderList")
                        .put(userId, seckillOrder);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SeckillOrder findOrderFromRedis(String userId) {
        try {
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveOrder(String userId, String transactionId) {
        try{
            /** 根据用户ID从redis中查询秒杀订单 */
            SeckillOrder seckillOrder = (SeckillOrder)redisTemplate
                    .boundHashOps("seckillOrderList").get(userId);
            /** 判断秒杀订单 */
            if(seckillOrder != null){
                /** 微信交易流水号 */
                seckillOrder.setTransactionId(transactionId);
                /** 支付时间 */
                seckillOrder.setPayTime(new Date());
                /** 状态码(已付款) */
                seckillOrder.setStatus("1");
                /** 保存到数据库 */
                seckillOrderMapper.insertSelective(seckillOrder);
                /** 删除Redis中的订单 */
                redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<SeckillOrder> findOrderByTimeout() {
        try {
            List<SeckillOrder> seckillOrders = new ArrayList<>();
            List<Object> seckillOrderList = redisTemplate
                    .boundHashOps("seckillOrderList").values();
            if(seckillOrderList != null && seckillOrderList.size() > 0){
                for (Object obj : seckillOrderList) {
                    SeckillOrder seckillOrder = (SeckillOrder) obj;
                    long endTime = new Date().getTime() - (5*60*1000);
                    if(seckillOrder.getCreateTime().getTime() < endTime){
                        seckillOrders.add(seckillOrder);
                    }
                }
            }
            return seckillOrders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteOrderFromRedis(SeckillOrder seckillOrder) {
        try {
            // 删除Redis缓存中的订单
            redisTemplate.boundHashOps("seckillOrderList")
                    .delete(seckillOrder.getUserId());
            /** ######## 恢复库存数量 #######*/
            // 从Redis查询秒杀商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate
                    .boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId());
            // 判断缓存中是否存在该商品
            if(seckillGoods != null){
                // 修改缓存中秒杀商品的库存
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            }else {
                // 从数据库查询该商品
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                // 设置秒杀商品库存数量
                seckillGoods.setStockCount(1);
            }
            redisTemplate.boundHashOps("seckillGoodsList").put(seckillOrder.getSeckillId(),seckillGoods);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
