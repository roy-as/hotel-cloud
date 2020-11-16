package com.hotel.cloud.common.vo;

import lombok.Data;

import java.util.Arrays;

@Data
public class CommandStatus {

    private byte[] downBody;

    private byte[] command;

    private String mac;

    private byte[] upBody;

    private boolean success;

    public String getUpBody() {
        return Arrays.toString(this.upBody);
    }
}
