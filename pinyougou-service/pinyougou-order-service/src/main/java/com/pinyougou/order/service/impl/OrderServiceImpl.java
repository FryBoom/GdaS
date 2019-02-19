package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public void save(Order order) {
        try {
            // 根据用户名获取Redis中购物车数据
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId()).get();

            /** 定义订单ID集合(一次支付对应多个订单) */
            List<String> orderIdList = new ArrayList<>();
            /** 定义多个订单支付的总金额（元） */
            double totalMoney = 0;

            // 迭代购物车数据
            for (Cart cart : carts) {
            /** ####### 往订单表插入数据 ######### */
            // 生成订单主键id
                long orderId = idWorker.nextId();
                // 创建新的订单
                Order order1 = new Order();
                // 设置订单id
                order1.setOrderId(orderId);
                // 设置支付类型
                order1.setPaymentType(order.getPaymentType());
                // 设置支付状态码为“未支付”
                order1.setStatus("1");
                // 设置订单创建时间
                order1.setCreateTime(new Date());
                // 设置订单修改时间
                order1.setUpdateTime(order1.getCreateTime());
                // 设置用户名
                order1.setUserId(order.getUserId());
                // 设置收件人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 设置收件人手机号码
                order1.setReceiverMobile(order.getReceiverMobile());
                // 设置收件人
                order1.setReceiver(order.getReceiver());
                // 设置订单来源
                order1.setSourceType(order.getSourceType());
                // 设置商家id
                order1.setSellerId(cart.getSellerId());

            // 定义该订单总金额
                double money = 0;
            /** ####### 往订单明细表插入数据 ######### */
                for (OrderItem orderItem : cart.getOrderItems()) {
                    // 设置主键id
                    orderItem.setId(idWorker.nextId());
                    // 设置关联的订单id
                    orderItem.setOrderId(orderId);
                    // 累计总金额
                    money += orderItem.getTotalFee().doubleValue();
                    // 保存数据到订单明细表
                    orderItemMapper.insertSelective(orderItem);
                }
            // 设置支付总金额
                order1.setPayment(new BigDecimal(money));
            // 保存数据到订单表
                orderMapper.insertSelective(order1);

                /** 记录订单id*/
                orderIdList.add(String.valueOf(orderId));
                /** 记录总金额 */
                totalMoney += money;
            }

            /** 判断是否为微信支付 */
            if("1".equals(order.getPaymentType())){
                /** 创建支付日志对象 */
                PayLog payLog = new PayLog();
                /** 生成订单交易号 */
                String outTradeNo = String.valueOf(idWorker.nextId());
                /** 设置订单交易号 */
                payLog.setOutTradeNo(outTradeNo);
                /** 创建时间 */
                payLog.setCreateTime(new Date());
                /** 支付总金额(分) */
                payLog.setTotalFee((long)(totalMoney * 100));
                /** 用户ID */
                payLog.setUserId(order.getUserId());
                /** 支付状态 */
                payLog.setTradeState("0");
                /** 订单号集合，逗号分隔 */
                String ids = orderIdList.toString().replace("[", "")
                        .replace("]", "").replace(" ","");
                /** 设置订单号 */
                payLog.setOrderList(ids);
                /** 支付类型 */
                payLog.setPayType("1");
                /** 往支付日志表插入数据 */
                payLogMapper.insertSelective(payLog);
                /** 存入缓存 */
                redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);
            }

            // 删除该用户购物车数据
            redisTemplate.delete("cart_"+order.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public List<Order> findByPage(Order order, int page, int rows) {
        return null;
    }

    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return (PayLog) redisTemplate.boundValueOps("payLog_"+userId).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transactionId) {
        try {
            /** 修改支付日志状态 */
            PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
            payLog.setPayTime(new Date());
            payLog.setTradeState("1");
            payLog.setTransactionId(transactionId);
            payLogMapper.updateByPrimaryKeySelective(payLog);
            /** 修改订单状态 */
            String[] orderIds = payLog.getOrderList().split(",");
            /** 循环订单号数组 */
            for (String orderId : orderIds) {
                Order order = new Order();
                order.setOrderId(Long.valueOf(orderId));
                order.setPaymentTime(new Date());
                order.setStatus("2");
                orderMapper.updateByPrimaryKeySelective(order);
            }
            /** 清除redis缓存数据 */
            redisTemplate.delete("payLog_"+payLog.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
