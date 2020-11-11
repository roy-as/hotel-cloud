package com.hotel.cloud.modules.sys.service.impl;

import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.mqtt.Mqtt;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.modules.sys.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WifiService implements MqttService {

    @Autowired
    private Mqtt mqtt;

    @Override
    public void publish(String target, String... data) {
        if(CommonUtils.isEmpty(data) || data.length < 2) {
            throw new RRException(ExceptionEnum.PARAM_ERROR);
        }
        String account = data[0];
        String password = data[1];
        mqtt.publish(target, account, password);
    }
}
