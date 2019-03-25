package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import exception.PinyougouException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;


    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    @Override
    public void submitOrder(Long seckillId, String userId) {
        // 1.查询秒杀商品
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(seckillId);
        if (seckillGoods == null) {
            throw new PinyougouException("秒杀商品不存在");
        }
        // 当前剩余库存数量
        int stockCount = seckillGoods.getStockCount();
        if (stockCount <= 0) {
            throw new PinyougouException("该商品已被抢光啦");
        }

        // 2.减库存
        seckillGoods.setStockCount(stockCount - 1);
        redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).put(seckillId, seckillGoods);
        if (seckillGoods.getStockCount() == 0) {
            // 将秒杀商品同步到数据库
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            // 清除缓存中该商品的数据
            redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).delete(seckillId);
        }

        // 3.保存订单到缓存
        TbSeckillOrder tbSeckillOrder = buildSeckillOrder(userId, seckillGoods);
        redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).put(userId, tbSeckillOrder);

    }

    /**
     * 构造秒杀订单
     *
     * @param userId       当前登录用户
     * @param seckillGoods 秒杀商品数据
     */
    private TbSeckillOrder buildSeckillOrder(String userId, TbSeckillGoods seckillGoods) {
        long orderId = idWorker.nextId();
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(orderId);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillGoods.getId());
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(userId);//设置用户ID
        seckillOrder.setStatus("0");//未支付状态

        return seckillOrder;
    }

    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(userId);
    }

    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        System.out.println("saveOrderFromRedisToDb:" + userId);
        //根据用户ID查询日志
        TbSeckillOrder seckillOrder =
                (TbSeckillOrder) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(userId);
        if (seckillOrder == null) {
            throw new PinyougouException("订单不存在");
        }
        //订单号不符
        if (seckillOrder.getId().longValue() != orderId.longValue()) {
            throw new PinyougouException("订单不相符");
        }
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态改为支付成功
        seckillOrderMapper.insert(seckillOrder);//保存到数据库
        redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).delete(userId);//从redis中清除
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //根据用户ID查询日志
        TbSeckillOrder seckillOrder =
                (TbSeckillOrder) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(userId);
        if (seckillOrder != null &&
                seckillOrder.getId().longValue() == orderId.longValue()) {
            redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
            //恢复库存
            //1.从缓存中提取秒杀商品
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.
                    boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(seckillOrder.getSeckillId());
            if (seckillGoods != null) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).
                        put(seckillOrder.getSeckillId(), seckillGoods);//存入缓存
            }
        }
    }


}
