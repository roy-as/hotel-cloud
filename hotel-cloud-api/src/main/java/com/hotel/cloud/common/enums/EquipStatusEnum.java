package com.hotel.cloud.common.enums;

public enum  EquipStatusEnum {

    DISABLE(0, "待绑定"),
    PENDING_OLD(1, "待老化"),
    PENDING_RELEASE(2, "待下发"),
    PENDING_SET(3, "待安装"),
    WORKING(4, "工作中"),
    RECYCLE(5, "已回收"),
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
