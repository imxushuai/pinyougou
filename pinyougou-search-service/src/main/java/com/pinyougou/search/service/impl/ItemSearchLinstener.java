package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 商品导入solr消息监听器
 * Author xushuai
 * Description
 */
@Component
public class ItemSearchLinstener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            // 接收消息
            TextMessage textMessage = (TextMessage) message;
            String json = textMessage.getText();
            // 将json转换为itemList对象
            List<TbItem> itemList = JSON.parseArray(json, TbItem.class);

            // 执行导入solr索引库操作
            itemSearchService.importList(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}

