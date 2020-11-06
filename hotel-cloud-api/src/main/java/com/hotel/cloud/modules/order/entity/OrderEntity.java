package com.hotel.cloud.modules.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
@Data
@TableName("t_order")
public class OrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单号
	 */
	@TableId(type = IdType.INPUT)
	private String id;
	/**
	 * 代理id
	 */
	private Long agentId;
	/**
	 * 代理名称
	 */
	private String agentName;
	/**
	 * 酒店id
	 */
	private Long hotelId;
	/**
	 * 酒店名称
	 */
	private String hotelName;
	/**
	 * 状态，0待审核，1待发货，2完成
	 */
	private Integer status;
	/**
	 * 安装公司id
	 */
	private Long installationId;
	/**
	 * 安装公司名称
	 */
	private String installationName;
	/**
	 * 物流单号
	 */
	private String deliveryNo;
	/**
	 * 物流公司
	 */
	private String delivery;
	/**
	 * 支付方式，1支付宝，2微信，3银行转账
	 */
	private Integer payType;
	/**
	 * 收款账号
	 */
	private String account;
	/**
	 * 收款人
	 */
	private String accountName;
	/**
	 * 支付金额
	 */
	private BigDecimal amount;
	/**
	 * 实际支付金额
	 */
	private BigDecimal realAmount;
	/**
	 * 优惠券序列号
	 */
	private String couponSn;
	/**
	 * 优惠券id
	 */
	private Long couponId;
	/**
	 * 支付流水号
	 */
	private String payOrderNo;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 审核人
	 */
	private String auditBy;
	/**
	 * 发货人
	 */
	private String deliveryBy;
	/**
	 * 备注
	 */
	private String remark;
}
