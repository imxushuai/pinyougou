package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 秒杀定时任务
 * Author xushuai
 * Description
 */
@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;


    /**
     * 定时增量更新秒杀商品列表
     * 执行周期：每分钟的 0 秒执行
     */
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods() {
//        System.out.println("Spring task run！" + new SimpleDateFormat("yyyy-mm-dd").format(new Date()));
        // 查询缓存中的秒杀商品集合
        Set set = redisTemplate.boundHashOps("seckillGoods").keys();
        // id集合
        ArrayList<Long> arrayList = new ArrayList<Long>(set);

        // 封装查询条件
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        // 查询出状态为已审核,库存大于0,秒杀时间在开始和结束时间之间且必须为新增的秒杀商品
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria().andStatusEqualTo(TbSeckillGoods.STATUS_CHECK)
                .andStockCountGreaterThan(0)
                .andStartTimeLessThanOrEqualTo(new Date()).andEndTimeGreaterThan(new Date());
        if (arrayList.size() > 0) {// 当缓存中已有数据
            // 只查询新增的
            criteria.andIdNotIn(arrayList);
        }

        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        // 将查询到的数据存入缓存
        if (seckillGoodsList.size() > 0) {
            for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }
        }
        System.out.println("新增" + seckillGoodsList.size() + "条秒杀商品数据");
    }

    /**
     * 定时移除缓存中已经结束秒杀的秒杀商品
     * 执行周期：每秒执行
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods() {
        // 查询缓存
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();

        // 遍历集合
        if (seckillGoods != null && seckillGoods.size() > 0) {
            for (TbSeckillGoods goods : seckillGoods) {
                // 结束时间的毫秒值小于当前时间的毫秒值
                if (goods.getEndTime().getTime() < new Date().getTime()) {
                    // 秒杀结束,更新数据到数据库
                    seckillGoodsMapper.updateByPrimaryKey(goods);
                    // 清除缓存中的数据
                    redisTemplate.boundHashOps("seckillGoods").delete(goods.getId());
                    System.out.println("ID为:" + goods.getId() + "的商品，秒杀结束");
                }
            }
        }
    }

}

