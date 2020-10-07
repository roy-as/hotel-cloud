package com.hotel.cloud.common.enums;

/**
 * 标志位
 */
public enum FlagEnum {

    DELETE(0, "删除"),
    OK(1, "正常");

    private Integer code;

    private String desc;

    FlagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
