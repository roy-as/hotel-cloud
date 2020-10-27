package com.hotel.cloud.common.enums;

public enum  ExceptionEnum {

    NOT_ALLOW_CREATE_CHILD_AGENT(1001, "不能闯将子代理"),
    LOGIN_USER_EXPITED(1002, "登陆用户失效"),
    OSS_CONFIG_NOT_EXIST(1003, "云配置不存在"),
    NOT_AUTHENTICATION(1004, "权限不足"),
    INTERNAL_SERVER_ERROR(1005, "二维码生成失败"),
    EQUIP_HAVE_QRCODE(1006, "设备已生成过二维码"),
    EXIST_ROOM(1007, "存在关联房间，不能删除"),
    EXIST_ROOM_TYPE(1008, "存在关联房型，不能删除"),
    EXIST_EQUIP(1009, "存在关联设备，不能删除"),
    RELATE_HOTEL(1008, "已下发酒店，不能删除"),
    ENUM_NOT_EXIST(1010, "不存在该枚举类型"),
    SERVICE_NOT_EXIST(1011, "服务不存在"),
    NOT_LESS_NOW(1022, "不能小于当前时间")
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
