package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Solr
 * Author xushuai
 * Description
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;


    public static void main(String[] args){
        ApplicationContext ac =
                new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) ac.getBean("solrUtil");
        // 执行导入
        solrUtil.importData();
    }

    /**
     * 导入商品数据到索引库
     */
    private void importData() {
        // 构造查询条件
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo(TbItem.STATUS_NORMAL);
        // 执行查询
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        // 处理规格属性
        for (TbItem item : tbItems) {
            Map specMap = JSON.parseObject(item.getSpec());
            item.setSpecMap(specMap);
        }

        // 导入数据到索引库
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

}

