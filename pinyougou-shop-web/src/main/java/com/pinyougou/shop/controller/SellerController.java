package com.pinyougou.shop.controller;

import java.util.List;

import entity.Password;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

import entity.PageResult;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeller> findAll() {
        return sellerService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return sellerService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seller
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeller seller) {
        try {
            // 密码加密
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 加密密码
            String encode = passwordEncoder.encode(seller.getPassword());
            //将加密后的密码设置会商家
            seller.setPassword(encode);
            //保存
            sellerService.add(seller);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seller
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeller seller) {
        try {
            sellerService.update(seller);
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
    public TbSeller findOne(String id) {
        return sellerService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(String[] ids) {
        try {
            sellerService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param seller
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeller seller, int page, int rows) {
        return sellerService.findPage(seller, page, rows);
    }


    /**
     * 修改密码
     *
     * @param password 修改密码实体
     * @return entity.Result
     */
    @RequestMapping("/updatePassword")
    public Result updatePassword(@RequestBody Password password) {
        try {
            // 对密码进行加密处理
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String newPwd = passwordEncoder.encode(password.getNewPwd());
            //获取当前登录的用户id
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            TbSeller seller = findOne(name);
            //校验两个密码是否一致
            if(BCrypt.checkpw(password.getOldPwd(),seller.getPassword())) {//一致
                sellerService.updatePassword(name, newPwd);
                return Result.success("修改密码成功");
            }

            return Result.error("原密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改密码失败");
        }
    }

}
