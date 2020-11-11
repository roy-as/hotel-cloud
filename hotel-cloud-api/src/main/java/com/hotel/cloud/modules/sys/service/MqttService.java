package com.hotel.cloud.modules.sys.service;

public interface MqttService {

    public void publish(String target, String ... data);
}
