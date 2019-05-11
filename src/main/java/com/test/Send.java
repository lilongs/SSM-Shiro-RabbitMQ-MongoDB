package com.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.test.entity.User;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


/**
 * created by 李龙 on 2019-04-26
 **/
public class Send implements Runnable {
    private final static String QUEUE_NAME="receiveQueue";
    private final CountDownLatch countDownLatch ;

    public Send(CountDownLatch countDownLatch) {
        super();
        this.countDownLatch = countDownLatch;
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for(int i=500;i>0;i--){
            new Thread(new Send(countDownLatch)).start();
        }
        countDownLatch.countDown();
    }

    public void run() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /**
         * 创建连接连接到MabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        //设置MabbitMQ所在主机ip或者主机名
        factory.setHost("127.0.0.1");
        try {
            //创建一个连接
            Connection connection = factory.newConnection();
            //创建一个频道
            Channel channel = connection.createChannel();
            // 声明一个队列 -// queue 队列名称
            // durable 为true时server重启队列不会消失 (是否持久化)
            // exclusive 队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
            // autoDelete 当没有任何消费者使用时，自动删除该队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            //发送的消息
            User user=new User();
            user.setUsername("lilong");
            user.setPassword("123") ;
            user.setFullname("li");
            user.setSex("1");
            user.setBirthday("1994-11-08");
            user.setAddress("安徽省芜湖市");
            user.setPhone("110");

            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(user);
            System.out.println(message);
            /*
             * 向server发布一条消息
             * 参数1：exchange名字，若为空则使用默认的exchange
             * 参数2：routing key
             * 参数3：其他的属性
             * 参数4：消息体
             * RabbitMQ默认有一个exchange，叫default exchange，它用一个空字符串表示，它是direct exchange类型，
             * 任何发往这个exchange的消息都会被路由到routing key的名字对应的队列上，如果没有对应的队列，则消息会被丢弃
             */
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + user + "'");
            //关闭频道和连接
            channel.close();
            connection.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
