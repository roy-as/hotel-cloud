package com.hotel.cloud.common.mqtt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.config.property.MqttProperty;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
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
import java.util.List;


@Slf4j
@Component
public class Mqtt implements ApplicationRunner {

    private static MqttClient client;

    // 0至多一次，不保证消息到达，可能会丢失或重复,1至少一次，确保消息到达，但是可能会有重复
    private final static int DEFAULT_QOS = 1;

    @Autowired
    private MqttProperty mqttProperty;

    @Autowired
    private EquipService equipService;

    public MqttProperty getMqttProperty() {
        return mqttProperty;
    }

    public void setMqttProperty(MqttProperty mqttProperty) {
        this.mqttProperty = mqttProperty;
    }

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

        // 查询设备
        List<EquipEntity> equips = equipService.list(new QueryWrapper<EquipEntity>()
                .eq("flag", FlagEnum.OK.getCode())
        );
        int length = equips.size() * 2;
        String[] topics = new String[length];
        try {
            int[] qosArray = new int[length];
            int qos = null != mqttProperty.getQos() ? mqttProperty.getQos() : DEFAULT_QOS;
            // 生成down和up的topic
            for (int i = 0; i < equips.size(); i++) {
                qosArray[2 * i] = qos;
                qosArray[2 * i + 1] = qos;
                EquipEntity equip = equips.get(i);
                topics[2 * i] = MessageFormat.format(mqttProperty.getSubscribeTopic(), equip.getMac());
                topics[2 * i + 1] = MessageFormat.format(mqttProperty.getPublishTopic(), equip.getMac());
            }

            client.setCallback(new TopicCallback(client, options, topics, qosArray));

            client.connect(options);
            if (CommonUtils.isNotEmpty(topics)) {
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
    public void publish(String target, byte[] message, int qos) throws Exception {
        if (null != client && client.isConnected()) {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setPayload(message);
            String topicName = MessageFormat.format(mqttProperty.getPublishTopic(), target.replaceAll("-", ""));
            System.out.println(Arrays.toString(message));
            MqttTopic topic = client.getTopic(topicName);

            if (null != topic) {
                log.info("发布主题:{},消息题为:{}", topicName, message);

                MqttDeliveryToken publish = topic.publish(mqttMessage);
                publish.waitForCompletion();
                if (publish.isComplete()) {
                    MqttWireMessage response = publish.getResponse();
                    byte[] payload = response.getPayload();
                    log.info("返回信息为:{}", new String(payload));
                }

            }
        } else {
            this.reConnect();
        }

    }

    public void publish(String topicName, String message, int qos) throws Exception {
        this.publish(topicName, CommonUtils.hexStr2bytes(message), qos);
    }

    /**
     * 发布消息
     *
     * @param target
     * @param data
     */
    public void publish(String target, String... data) throws Exception {
        this.publish(target, CommonUtils.string2Bytes(data), 1);
    }

    public void subscribe(String target) throws MqttException {
        client.subscribe(target, DEFAULT_QOS);
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

    public static void main(String[] args) throws Exception {
        MqttProperty property = new MqttProperty();
        property.setServerUri("tcp://yunzhu.store:1883");
        property.setClientId("hebo_test");
        property.setUsername("YunZhuUserName_Az1");
        property.setPassword("yun_zhu_password_Az1");
        property.setSubscribeTopic("/yunzhuA/84cca884a084/up");
        property.setPublishTopic("/yunzhuA/{0}/down");
        property.setQos(1);
        Mqtt mqtt = new Mqtt();
        mqtt.setMqttProperty(property);
        mqtt.connect();

        //mqtt.subscribe("123456789");
       // mqtt.subscribe("40f52022b40a");
        byte[] message = {0x25, 0x06, 89, 117, 110, 122, 104, 117, 12, 64, 89, 117, 110, 122, 104, 117, 65, 100, 109, 105, 110};
        mqtt.publish("123456789", message, 1);

        //System.out.println("/yunzhuA/84cca884a084/up".split("/")[2]);
    }
}
