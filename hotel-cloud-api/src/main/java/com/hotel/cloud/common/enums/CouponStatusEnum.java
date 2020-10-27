package com.hotel.cloud.common.enums;

public enum  CouponStatusEnum {
    NOT_USED(0, "未使用"),
    USED(1, "已使用"),
    EXPIRED(2, "已过期")
    ;

    private Integer code;

    private String desc;

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

    CouponStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
