package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import exception.PinyougouException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 获取品牌列表
     *
     * @return java.util.List<com.pinyougou.pojo.TbBrand>
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 获取品牌列表，分页显示
     *
     * @param page 当前页码
     * @param size 每页显示记录数
     * @return entity.PageResult
     */
    @RequestMapping("/findByPage")
    public PageResult findByPage(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return brandService.findByPage(page, size);
    }

    /**
     * 新增品牌
     *
     * @param brand 品牌数据
     * @return entity.Result
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return Result.success("新增成功");
        } catch (RuntimeException e) {
            if (e instanceof PinyougouException) {
                return Result.error(e.getMessage());
            }
            e.printStackTrace();
            return Result.error("新增失败");
        }
    }

    /**
     * 加载指定id的品牌
     *
     * @param id 品牌id
     * @return com.pinyougou.pojo.TbBrand
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(long id) {
        return brandService.findOne(id);
    }

    /**
     * 修改品牌
     *
     * @param brand 品牌数据
     * @return entity.Result
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return Result.success("修改成功");
        } catch (Exception e) {
            if (e instanceof PinyougouException) {
                return Result.error(e.getMessage());
            }
            e.printStackTrace();
            return Result.error("修改失败");
        }
    }

    /**
     * 删除品牌
     *
     * @param ids 被删除品牌的品牌id
     * @return entity.Result
     */
    @RequestMapping("/delete")
    public Result delete(long[] ids) {
        try {
            brandService.delete(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            if (e instanceof PinyougouException) {
                return Result.error(e.getMessage());
            }
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }

    /**
     * 按条件获取品牌列表，分页显示
     *
     * @param brand 查询条件
     * @param page  当前页码
     * @param size  每页显示记录数
     * @return entity.PageResult
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return brandService.findByPage(brand, page, size);
    }

    /**
     * 获取品牌下拉列表需要的品牌列表数据格式
     *
     * @return java.util.List<java.util.Map>
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }

}
