package com.hotel.cloud.common.vo;

import lombok.Data;

import java.util.Arrays;
import java.util.Map;

@Data
public class CommandStatus {

    private byte[] downBody;

    private byte[] command;

    private String mac;

    private byte[] upBody;

    private boolean success;

    private Map<String, Object> data;

    public void setUpBody(byte[] upBody) {
        this.upBody = upBody;
    }
}
