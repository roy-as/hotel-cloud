package com.hotel.cloud.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperty {

    private String username;

    private String password;

    private String serverUri;

    private String clientId;

    private String subscribeTopic = "";

    private String publishTopic;

    private Integer timeOut;

    private Integer qos;
}
