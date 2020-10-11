package com.hotel.cloud.common.enums;

public enum  EquipStatusEnum {

    DISABLE(0, "不可用"),
    PENDING_RELEASE(1, "待下发"),
    RELEASED(2, "已绑定"),
    RECYCLE(3, "已回收")
    ;

    private Integer code;

    private String desc;

    EquipStatusEnum(Integer code, String desc) {
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
