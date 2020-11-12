package com.hotel.cloud.common.mqtt;

import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.config.property.MqttProperty;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.text.MessageFormat;
import java.util.Arrays;


@Slf4j
@Component
public class Mqtt implements ApplicationRunner {

    private static MqttClient client;

    // 0至多一次，不保证消息到达，可能会丢失或重复,1至少一次，确保消息到达，但是可能会有重复
    private final static int DEFAULT_QOS = 1;

    @Autowired
    private MqttProperty mqttProperty;


    public Mqtt() {
    }

    public Mqtt(MqttProperty mqttProperty) {
        this.mqttProperty = mqttProperty;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化mqtt连接");
        connect();
    }

    /**
     * 连接服务器
     *
     * @throws MqttException
     */
    public void connect() throws MqttException {
        if (null == mqttProperty) {
            return;
        }
        client = new MqttClient(mqttProperty.getServerUri(), mqttProperty.getClientId(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(mqttProperty.getUsername());
        options.setPassword(mqttProperty.getPassword().toCharArray());
        options.setConnectionTimeout(null != mqttProperty.getTimeOut() ? mqttProperty.getTimeOut() : 30);
        options.setKeepAliveInterval(20);
        String[] topics = mqttProperty.getSubscribeTopic().split(",");
        try {
            int[] qosArray = new int[topics.length];
            int qos = null != mqttProperty.getQos() ? mqttProperty.getQos() : DEFAULT_QOS;
            for(int i = 0; i < topics.length; i++) {
                qosArray[i] = qos;
            }
            if(CommonUtils.isNotEmpty(topics)) {
                client.setCallback(new TopicCallback(client, options, topics, qosArray));
            }
            client.connect(options);
            if(CommonUtils.isEmpty(topics)) {
                client.subscribe(topics, qosArray);
            }
            log.info("mqtt连接成功");
        } catch (Exception e) {
            log.error("连接异常", e);
        }
    }

    /**
     * 发布消息
     *
     * @param target
     * @param message
     * @param qos
     */
    public void publish(String target, byte[] message, int qos) {
        if (null != client && client.isConnected()) {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setPayload(message);
            String topicName = MessageFormat.format(mqttProperty.getPublishTopic(), target.replaceAll("-", ""));
            System.out.println(Arrays.toString(message));
            MqttTopic topic = client.getTopic(topicName);

            if (null != topic) {
                log.info("发布主题:{},消息题为:{}", topicName, message);
                try {
                    MqttDeliveryToken publish = topic.publish(mqttMessage);
                    publish.waitForCompletion();
                    if (publish.isComplete()) {
                        log.info("消息发布成功:"+ new String(message));
                        MqttWireMessage response = publish.getResponse();
                        byte[] payload = response.getPayload();
                        log.info("返回信息为:" + new String(payload));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.reConnect();
        }

    }

    public void publish(String topicName, String message, int qos) {
        this.publish(topicName, CommonUtils.hexStr2bytes(message), qos);
    }

    /**
     * 发布消息
     * @param target
     * @param data
     */
    public void publish(String target, String ... data) {
        this.publish(target, CommonUtils.string2Bytes(data), 1);
    }


    /**
     * 重连
     */
    private void reConnect() {
        if (!client.isConnected()) {
            while (true) {
                try {
                    Thread.sleep(30000);
                    connect();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
