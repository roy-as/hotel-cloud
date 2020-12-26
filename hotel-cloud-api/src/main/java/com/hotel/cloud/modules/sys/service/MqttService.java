package com.hotel.cloud.modules.sys.service;

import com.hotel.cloud.common.vo.CommandStatus;
import org.eclipse.paho.client.mqttv3.MqttException;

public interface MqttService {

    CommandStatus publish(String target, byte[] command, Integer commandType, String ... data) throws Exception;

    CommandStatus publish(String target, byte[] command, byte[] data) throws Exception;

    void subscribe(String target) throws MqttException;
}
