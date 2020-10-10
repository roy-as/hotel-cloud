package com.hotel.cloud.common.enums;

public enum  ExceptionEnum {

    NOT_ALLOW_CREATE_CHILD_AGENT(1001, "不能闯将子代理"),
    LOGIN_USER_EXPITED(1002, "登陆用户失效"),
    OSS_CONFIG_NOT_EXIST(1003, "云配置不存在"),
    NOT_AUTHENTICATION(1004, "权限不足"),
    INTERNAL_SERVER_ERROR(1005, "二维码生成失败"),
    EQUIP_HAVE_QRCODE(1006, "设备已生成过二维码"),
    ;

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
