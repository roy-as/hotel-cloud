package com.hotel.cloud.common.mqtt;

import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.RedisUtils;
import com.hotel.cloud.common.utils.SpringContextUtils;
import com.hotel.cloud.common.vo.CommandStatus;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TopicCallback implements MqttCallback {

    private static RedisUtils redisUtils;

    static {
        redisUtils = SpringContextUtils.getBean("redisUtils", RedisUtils.class);
    }

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
                Thread.sleep(5000);
                client.connect(options);
                log.info("MQTT重新连接成功:{}", client);
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
        String mac = topic.split("/")[2];
        String redisKey = MessageFormat.format(Constants.REDIS_COMMAND_KEY, mac);
        CommandStatus commandStatus = redisUtils.get(redisKey, CommandStatus.class);
        if(null == commandStatus) {
            commandStatus = new CommandStatus();
            commandStatus.setMac(mac);
        }
        if (topic.endsWith("down")) {
            commandStatus.setDownBody(payload);
        } else if (topic.endsWith("up")) {
            commandStatus.setCommand(payload);
            commandStatus.setUpBody(payload);
        }
        redisUtils.set(redisKey, commandStatus, 10 * 1000);
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
