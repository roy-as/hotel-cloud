package com.hotel.cloud.common.enums;

public enum  ExceptionEnum {

    NOT_ALLOW_CREATE_CHILD_AGENT(1001, "不能闯将子代理"),
    LOGIN_USER_EXPITED(1002, "登陆用户失效")

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
