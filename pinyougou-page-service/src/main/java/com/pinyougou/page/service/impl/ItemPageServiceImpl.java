package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品详情页静态化实现
 * Author xushuai
 * Description
 */
@Service
@Transactional
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer FreeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Value("${PAGE_DIR}")
    private String PAGE_DIR;

    @Override
    public boolean genItemPage(Long goodsId) {
        try {
            // 获取数据
            Map<String, Object> data = getDataMap(goodsId);

            // 创建文件输出流
            Writer out = new FileWriter(PAGE_DIR + goodsId + ".html");

            // 使用模板生成商品详细页
            Configuration configuration = FreeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            template.process(data, out);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteItemPage(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                // 删除文件夹中的文件
                new File(PAGE_DIR + goodsId + ".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取模板需要的数据
     * 
     * @param goodsId 商品ID
     * @return java.util.Map<java.lang.String,java.lang.Object> 
     */
    private Map<String, Object> getDataMap(Long goodsId) {
        Map<String, Object> data = new HashMap<>();
        // 查询商品基本信息和商品扩展信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        // 查询商品分类信息
        String category1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
        String category2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
        String category3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
        // 查询SKU列表
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId).andStatusEqualTo(TbItem.STATUS_NORMAL);
        example.setOrderByClause("is_default DESC");
        List<TbItem> itemList = itemMapper.selectByExample(example);

        // 将查询到的数据封装到Map
        data.put("goods", tbGoods);
        data.put("goodsDesc", tbGoodsDesc);
        // 商品分类数据
        data.put("category1", category1);
        data.put("category2", category2);
        data.put("category3", category3);
        // SKU列表
        data.put("itemList", itemList);

        return data;
    }
}

