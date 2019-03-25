package com.pinyougou.seckill.controller;

import java.util.List;

import com.pinyougou.seckill.service.SeckillOrderService;
import exception.PinyougouException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return seckillOrderService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.add(seckillOrder);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seckillOrder
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.update(seckillOrder);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillOrderService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param seckillOrder
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeckillOrder seckillOrder, int page, int rows) {
        return seckillOrderService.findPage(seckillOrder, page, rows);
    }

    /**
     * 提交订单
     *
     * @param seckillId 秒杀商品ID
     * @return entity.Result
     */
    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if("anonymousUser".equals(username)){//如果未登录
            return new Result(false, "您还没有登录");
        }

        try {
            seckillOrderService.submitOrder(seckillId, username);
            return Result.success("订单提交成功");
        } catch (Exception e) {
            if (e instanceof PinyougouException) {
                return Result.error(e.getMessage());
            }
            e.printStackTrace();
            return Result.error("服务器系统错误");
        }
    }

}
