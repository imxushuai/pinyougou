package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 品牌相关接口
 *
 * @author Administrator
 */
public interface BrandService {

    /**
     * 获取品牌列表
     *
     * @return java.util.List<com.pinyougou.pojo.TbBrand>
     */
    List<TbBrand> findAll();

    /**
     * 分页查询品牌列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @return entity.PageResult
     */
    PageResult findByPage(int pageNum, int pageSize);

    /**
     * 新增品牌
     *
     * @param brand 品牌对象
     */
    void add(TbBrand brand);

    /**
     * 按 id 获取品牌
     *
     * @param id 品牌id
     * @return com.pinyougou.pojo.TbBrand
     */
    TbBrand findOne(long id);

    /**
     * 更新品牌
     *
     * @param brand 要更新的品牌
     */
    void update(TbBrand brand);

    /**
     * 删除品牌
     *
     * @param ids 被删除品牌的品牌id
     */
    void delete(long[] ids);

    /**
     * 按条件查询品牌列表
     *
     * @param brand 查询条件
     * @param page  当前页码
     * @param size  每页显示记录数
     * @return entity.PageResult
     */
    PageResult findByPage(TbBrand brand, int page, int size);

    /**
     * 返回品牌下拉列表数据
     *
     * @return java.util.List<java.util.Map>
     */
    List<Map> selectOptionList();
}
