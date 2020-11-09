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


//@Component
@Slf4j
public class Mqtt implements ApplicationRunner {

    private MqttClient client;

    private MqttConnectOptions options;
    // 0至多一次，不保证消息到达，可能会丢失或重复,1至少一次，确保消息到达，但是可能会有重复
    private final static int DEFAULT_QOS = 1;

    @Autowired
    private MqttProperty mqttProperty;

    public void setMqttProperty(MqttProperty mqttProperty) {
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
        this.client = new MqttClient(mqttProperty.getServerURI(), mqttProperty.getClientId(), new MemoryPersistence());
        this.options = new MqttConnectOptions();
        this.options.setCleanSession(false);
        this.options.setUserName(mqttProperty.getUsername());
        this.options.setPassword(mqttProperty.getPassword().toCharArray());
        this.options.setConnectionTimeout(null != mqttProperty.getTimeOut() ? mqttProperty.getTimeOut() : 30);
        this.options.setKeepAliveInterval(20);
        String[] topics = mqttProperty.getSubscribeTopic().split(",");
        try {
            int[] qosArray = new int[topics.length];
            int qos = null != mqttProperty.getQos() ? mqttProperty.getQos() : DEFAULT_QOS;
            for(int i = 0; i < topics.length; i++) {
                qosArray[i] = qos;
            }
            if(CommonUtils.isNotEmpty(topics)) {
                client.setCallback(new TopicCallback(client, this.options, topics, qosArray));
            }
            client.connect(this.options);
            client.subscribe(topics, qosArray);
            log.info("mqtt连接成功");
        } catch (Exception e) {
            log.error("连接异常", e);
        }
    }

    /**
     * 发布消息
     *
     * @param topicName
     * @param message
     * @param qos
     */
    public void publish(String topicName, byte[] message, int qos) {
        if (null != this.client && this.client.isConnected()) {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setPayload(message);

            MqttTopic topic = client.getTopic(topicName);

            if (null != topic) {
                try {
                    MqttDeliveryToken publish = topic.publish(mqttMessage);
                    publish.waitForCompletion();
                    if (publish.isComplete()) {
                        log.info("消息发布成功:{0}", new String(message));
                        MqttWireMessage response = publish.getResponse();
                        byte[] payload = response.getPayload();
                        log.info("返回信息为:{0}", new String(payload));
                    }
                } catch (MqttException e) {
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


    //	重新连接
    private void reConnect() {
        if (!client.isConnected()) {
            while (true) {
                try {
                    Thread.sleep(30000);
                    connect();
                    break;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws MqttException {
        MqttProperty property = new MqttProperty();
        property.setServerURI("tcp://yunzhu.store:1883");
        property.setClientId("hebo_test");
        property.setUsername("YunZhuUserName_Az1");
        property.setPassword("yun_zhu_password_Az1");
        property.setSubscribeTopic("/yunzhuA/12F1CEB8A989/up");
        property.setQos(1);
        Mqtt mqtt = new Mqtt();
        mqtt.setMqttProperty(property);
        mqtt.connect();
        byte[] message = {0x25, 0x04, 0x59, 0x6f, 0x67, 0x6f, 0x08, 0x79, 0x79, 0x67, 0x67, 0x31, 0x32, 0x33, 0x34};
        mqtt.publish("/yunzhuA/12F1CEB8A989/down",message, 1);
    }
}
