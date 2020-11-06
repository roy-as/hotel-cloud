package com.hotel.cloud.common.enums;

public enum  OrderStatusEnum {
    REJECT(-1, "已拒绝"),
    PENDING_AUDIT(0, "待审核"),
    PASS(1, "通过"),
    DELIVERY(2,"已发货"),
    INSTALLED(3, "已安装"),
    CONFIRM_INSTALL(4,"确认已安装"),
    PAID(5, "已付款，完成")
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

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
