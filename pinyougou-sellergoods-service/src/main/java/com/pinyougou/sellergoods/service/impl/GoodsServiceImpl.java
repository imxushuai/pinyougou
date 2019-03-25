package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        /***************************
         * 保存商品基本信息到goods表 *
         **************************/
        // 补全商品基本信息
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus(TbGoods.STATUS_CREATE);
        // 保存商品基本信息
        goodsMapper.insert(tbGoods);


        /*******************************
         * 保存商品扩展信息到goodsdesc表 *
         *******************************/
        // 补全商品扩展信息数据
        TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        // 保存商品扩展信息
        goodsDescMapper.insert(tbGoodsDesc);


        /*******************************
         *   保存SKU商品信息到 item表    *
         *******************************/
        saveItemList(goods, tbGoods, tbGoodsDesc);


    }

    /**
     * 保存SKU商品信息
     *
     * @param goods       组合商品对象
     * @param tbGoods     商品基本信息对象
     * @param tbGoodsDesc 商品扩展信息对象
     */
    private void saveItemList(Goods goods, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        // 判断是否启用规格
        if (tbGoods.getIsEnableSpec().equals(TbGoods.ISENABLESPEC_YES)) {// 启用规格
            // 遍历itemList，补全数据并保存
            for (TbItem item : goods.getItemList()) {
                // 设置标题
                item.setTitle(builderTitle(tbGoods, item).toString());
                // 补全其他数据
                setValues(tbGoods, tbGoodsDesc, item);

                // 保存SKU商品信息
                itemMapper.insert(item);
            }
        } else {// 未启用规格
            TbItem item = new TbItem();
            // 设置标题，直接为商品名称
            item.setTitle(tbGoods.getGoodsName());
            // 设置价格
            item.setPrice(tbGoods.getPrice());
            // 设置库存数量
            item.setNum(1000);
            // 设置商品状态
            item.setStatus(TbItem.STATUS_NORMAL);
            // 设置状态为默认
            item.setIsDefault(TbItem.DEFAULT_YES);
            // 设置规格为空
            item.setSpec("{}");
            // 补全其他数据
            setValues(tbGoods, tbGoodsDesc, item);

            // 保存SKU商品信息
            itemMapper.insert(item);
        }

    }

    /**
     * 补全item数据
     *
     * @param tbGoods     当前保存的商品
     * @param tbGoodsDesc 商品的扩展信息
     * @param item        SKU商品对象
     */
    private void setValues(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc, TbItem item) {
        // 设置商品分类ID
        item.setCategoryid(tbGoods.getCategory3Id());
        // 设置创建日期和更新日期
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        // 设置商品ID
        item.setGoodsId(tbGoods.getId());
        // 设置商家ID
        item.setSeller(tbGoods.getSellerId());
        // 设置商品分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        item.setCategory(itemCat.getName());
        // 设置品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
        item.setBrand(brand.getName());
        // 设置商家名称(店铺名)
        TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
        item.setSeller(seller.getNickName());
        // 设置图片
        List<Map> mapList = JSON.parseArray(tbGoodsDesc.getItemImages(), Map.class);
        if (mapList.size() > 0) {
            item.setImage(String.valueOf(mapList.get(0).get("url")));
        }
    }


    /**
     * 构造SKU商品信息的标题
     *
     * @param tbGoods 当前保存的商品
     * @param item    SKU商品对象
     * @return java.lang.StringBuilder
     */
    private StringBuilder builderTitle(TbGoods tbGoods, TbItem item) {
        // 使用tbGoods和规格选项构造SKU名称
        StringBuilder stringBuilder = new StringBuilder(tbGoods.getGoodsName()).append(" ");
        Map<String, Object> spec = JSON.parseObject(item.getSpec());
        for (String key : spec.keySet()) {
            stringBuilder.append(spec.get(key)).append(" ");
        }
        return stringBuilder;
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        /***************************
         * 保存商品基本信息到goods表 *
         **************************/
        // 补全商品基本信息
        TbGoods tbGoods = goods.getGoods();
        // 保存商品基本信息
        goodsMapper.updateByPrimaryKey(tbGoods);


        /*******************************
         * 保存商品扩展信息到goodsdesc表 *
         *******************************/
        // 补全商品扩展信息数据
        TbGoodsDesc tbGoodsDesc = goods.getGoodsDesc();
        // 保存商品扩展信息
        goodsDescMapper.updateByPrimaryKey(tbGoodsDesc);

        /*******************************
         *   保存SKU商品信息到 item表    *
         *******************************/
        // 删除原有SKU商品列表
        TbItemExample itemExample = new TbItemExample();
        itemExample.createCriteria().andGoodsIdEqualTo(tbGoods.getId());
        itemMapper.deleteByExample(itemExample);
        // 再进行保存
        saveItemList(goods, tbGoods, tbGoodsDesc);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        // 返回的组合实体类
        Goods goods = new Goods();

        // 获取商品的基本信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        // 获取商品的扩展信息
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        // 获取SKU商品列表信息
        TbItemExample itemExample = new TbItemExample();
        itemExample.createCriteria().andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(itemExample);

        // 将查询到的数据保存到组合实体类中
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setItemList(itemList);

        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            // 假删除
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete(TbGoods.ISDELETE_YES);
            // 保存修改
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        // 不查询被删除的商品
        criteria.andIsDeleteIsNull();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (long id : ids) {
            // 按ID查询商品
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            // 修改状态并保存
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        // 创建查询条件
        TbItemExample example = new TbItemExample();
        // 封装查询条件
        example.createCriteria().andGoodsIdIn(Arrays.asList(ids)).andStatusEqualTo(status);

        return itemMapper.selectByExample(example);
    }

}
