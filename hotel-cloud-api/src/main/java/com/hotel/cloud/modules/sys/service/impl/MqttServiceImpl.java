package com.hotel.cloud.modules.sys.service.impl;

import com.hotel.cloud.common.mqtt.Mqtt;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.RedisUtils;
import com.hotel.cloud.common.vo.CommandStatus;
import com.hotel.cloud.config.property.MqttProperty;
import com.hotel.cloud.modules.sys.service.MqttService;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;

@Service
public class MqttServiceImpl implements MqttService {

    @Autowired
    private Mqtt mqtt;

    @Autowired
    private MqttProperty mqttProperty;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public CommandStatus publish(String target, byte[] commands, Integer commandType, String... data) throws Exception {
        byte[] body =  commands;
        if(null != data) {
            byte[] bytes = commandType == 2 ? CommonUtils.hexStr2bytes(data[0].replaceAll(",","")): CommonUtils.string2Bytes(data);
            body = ArrayUtils.addAll(commands, bytes);
        }
        long start = System.currentTimeMillis();
        this.mqtt.publish(target, body, 1);
        String redisKey = MessageFormat.format(Constants.REDIS_COMMAND_KEY, target);
        CommandStatus commandStatus = null;
        while (System.currentTimeMillis() - start <= 5000) {
            commandStatus = redisUtils.get(redisKey, CommandStatus.class);
            if (null != commandStatus && null != commandStatus.getCommand()) {
                commandStatus.setSuccess(ArrayUtils.isEquals(body, commandStatus.getDownBody()) && ArrayUtils.isEquals(commands, ArrayUtils.subarray(commandStatus.getCommand(), 0, commands.length)));
                return commandStatus;
            }
        }
        if(null == commandStatus) {
           commandStatus = new CommandStatus();
           commandStatus.setMac(target);
        }
        commandStatus.setSuccess(false);
        return commandStatus;

    }

    @Override
    public void subscribe(String target) throws MqttException {
        mqtt.subscribe(MessageFormat.format(mqttProperty.getSubscribeTopic(), target));
        mqtt.subscribe(MessageFormat.format(mqttProperty.getPublishTopic(), target));
    }
}
