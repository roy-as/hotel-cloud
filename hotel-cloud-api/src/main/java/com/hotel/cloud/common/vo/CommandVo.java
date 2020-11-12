package com.hotel.cloud.common.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommandVo {

    @NotNull(message = "指令不能为空")
    private Long commandId;

    private String[] datas;

    @NotNull(message = "mac地址不能为空")
    private String[] macs;
}
