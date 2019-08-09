package com.example.yfsl.rabbitmq_demo;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 消息推送
 * RabbitMQ 后台管理器地址：localhost：15672 可以查看消息的情况
 */

public class RabbitMQUtils2 {
    private static String userName = "sa";//用户名
    private static String passWord = "123456";//用户密码
    private static String hostName = "192.168.40.2";//主机IP
    private static int portNum = 5672;//端口
    private static String exchangeName = "amq.direct";//交换机名称
    private static ConnectionFactory factory = new ConnectionFactory();//连接工厂对象 创建新连接
    private static final String routingKey = "text";//路由名称
    private static String queueName = "test_queue";//队列名称  接收端和发送端分别从指定的队列去去消息

    /**
     * Rabbit配置
     */
    public static void setupConnectionFactory() {
        factory.setUsername(userName);//用户名
        factory.setPassword(passWord);//密码
        factory.setHost(hostName);//主机ip
        factory.setPort(portNum);//端口
    }

    /**
     * 发消息
     */
    public static void basicPublish() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //连接 通过连接工厂创建一个新连接 与接收端不是同一个
                    Connection connectionP = factory.newConnection();
                    //通道 通过新连接创建一个新通道 与接收端也不是同一个
                    Channel channelP = connectionP.createChannel();
                    //声明了一个交换和一个服务器命名的队列，然后将它们绑定在一起
                    channelP.exchangeDeclare(exchangeName,"direct",true);//声明exchange
                    channelP.queueBind(queueName,exchangeName,routingKey);//将exchange和queue绑定
                    //消息发布
                    byte[] msg = "hello world!".getBytes();
                    //rountingKey 自己任意填写
                    channelP.basicPublish(exchangeName, routingKey, null, msg);
                    //关闭通道
                    channelP.close();
                    //关闭连接
                    connectionP.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 收消息
     */

    public static void basicConsume() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //连接 通过连接工厂创建一个新连接 与发送端不是同一个
                    Connection connectionC = factory.newConnection();
                    //通道 通过新连接创建一个新通道 与发送端也不是同一个
                    final Channel channelC = connectionC.createChannel();
                    //声明了一个交换和一个服务器命名的队列，然后将它们绑定在一起。
                    channelC.exchangeDeclare(exchangeName, "direct", true);
                    channelC.queueBind(queueName, exchangeName, routingKey);
                    //实现Consumer的最简单方法是将便捷类DefaultConsumer子类化。可以在basicConsume 调用上传递此子类的对象以设置订阅：
                    channelC.basicConsume(queueName, false, "administrator", new DefaultConsumer(channelC) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            super.handleDelivery(consumerTag, envelope, properties, body);
                            //接收到的消息
                            String msg = new String(body, "UTF-8");
                            //交付标记
                            long deliveryTag = envelope.getDeliveryTag();
                            channelC.basicAck(deliveryTag, false);
                            Log.e("TAG","接收的消息:"+msg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
