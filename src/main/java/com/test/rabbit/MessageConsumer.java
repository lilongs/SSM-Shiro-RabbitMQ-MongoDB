package com.test.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.entity.User;
import com.test.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * created by 李龙 on 2019-04-26
 * 用于接收指定队列的信息
 **/
public class MessageConsumer implements MessageListener {
    @Autowired
    private UserService userService;

    private Logger logger=LoggerFactory.getLogger(MessageListener.class);


    public void onMessage(Message message) {
        logger.info("consumer receive message------->:{}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        try {
            //获取到信息并将字符转为UTF-8
            String json=new String(message.getBody(),"UTF-8");
            System.out.println(json);
            user = objectMapper.readValue(json, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userService.insertUser(user);
    }
}
