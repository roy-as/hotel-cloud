package com.hotel.cloud.common.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Slf4j
public class TopicCallback implements MqttCallback {

    private MqttClient client;

    private MqttConnectOptions options;

    private String[] topics;

    private int[] qos;


    public TopicCallback() {
    }

    public TopicCallback(MqttClient client, MqttConnectOptions options, String[] topics, int[] qos) {
        this.client = client;
        this.options = options;
        this.topics = topics;
        this.qos = qos;
    }

    /**
     * 断线重连
     *
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.info("MQTT连接断开，发起重连");
        while (true) {
            try {
                Thread.sleep(30000);
                client.connect(options);
                //订阅消息
                client.subscribe(topics, qos);
                log.info("MQTT重新连接成功:{0}", client);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //订阅消息字符
        byte[] payload = message.getPayload();
        log.info("topic:{},payload:{}", topic, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    //对象转化为字节码
    private byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        return bo.toByteArray();
    }
}
