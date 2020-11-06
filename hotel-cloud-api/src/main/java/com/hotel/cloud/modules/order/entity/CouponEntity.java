package com.hotel.cloud.modules.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-25 11:06:20
 */
@Data
@TableName("t_coupon")
public class CouponEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	@JsonIgnore
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 优惠券类型，1:满减，2每满减，3:折扣,4:定额
	 */
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
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 订单id
	 */
	private String orderId;
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

	/**
	 * 使用范围 0所有，1智能主机，2智能设备
	 */
	private Integer scope;
	/**
	 * 实际金额
	 */
	@TableField(exist = false)
	private BigDecimal realAmount;

	/**
	 * 总金额
	 */
	@TableField(exist = false)
	private BigDecimal totalAmount;
}
