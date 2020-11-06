package com.hotel.cloud.common.vo.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponVo {

    /**
     * 主键
     */
    private Long id;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;
    /**
     * 优惠券类型，1:满减，2每满减，3:折扣,4:定额
     */
    @NotNull(message = "优惠券类型不能为空")
    private Integer type;
    /**
     * 序列号
     */
    private String sn;
    /**
     * 状态，0未使用，1已使用，2过期
     */
    private Integer status;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 满减金额
     */
    private BigDecimal amount;
    /**
     * 折扣
     */
    private BigDecimal discount;
    /**
     * 优惠金额
     */
    private BigDecimal couponAmount;
    /**
     * 过期时间
     */
    private Date expiredTime;
    @NotNull(message = "优惠券数量不能为空")
    private Integer count;

    /**
     * 使用范围 0所有，1智能主机，2智能设备
     */
    private Integer scope;
}
