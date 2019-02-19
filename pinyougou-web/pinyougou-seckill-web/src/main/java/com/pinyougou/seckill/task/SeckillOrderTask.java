package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SeckillOrderTask {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    @Scheduled(cron = "0/3 * * * * ?")
    public void closeOrderTask(){
        List<SeckillOrder> seckillOrders = seckillOrderService.findOrderByTimeout();
        for (SeckillOrder seckillOrder : seckillOrders) {
           Map<String,String> map = weixinPayService.closePayTimeout(seckillOrder.getId().toString());
           if("SUCCESS".equals(map.get("result_code"))){
               seckillOrderService.deleteOrderFromRedis(seckillOrder);
           }
        }
    }
}
