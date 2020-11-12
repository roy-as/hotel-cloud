package com.hotel.cloud.modules.sys.service.impl;

import com.hotel.cloud.common.mqtt.Mqtt;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.modules.sys.service.MqttService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttServiceImpl implements MqttService {

    @Autowired
    private Mqtt mqtt;

    @Override
    public void publish(String target, byte[] commands, String... data) {
        byte[] bytes = CommonUtils.string2Bytes(data);
        byte[] body = ArrayUtils.addAll(commands, bytes);
        this.mqtt.publish(target,  body, 1);
    }
}
