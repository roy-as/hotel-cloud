package com.hotel.cloud.common.vo.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponValidateVo {

    private Long id;

    // 购买数量
    private Integer shopNumber;

    // 设备类型， 1:智能主机，2:智能设备
    private Integer deviceType;

    // 价格
    BigDecimal price;
}
