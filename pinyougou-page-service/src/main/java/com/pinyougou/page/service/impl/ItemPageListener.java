package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 生成商品详细页消息监听器
 * Author xushuai
 * Description
 */
@Component
public class ItemPageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        // 接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long goodsId = (Long) objectMessage.getObject();
            // 执行生成商品详细页
            itemPageService.genItemPage(goodsId);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}

