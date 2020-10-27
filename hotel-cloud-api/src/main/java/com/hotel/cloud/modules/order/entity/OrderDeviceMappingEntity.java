package com.hotel.cloud.modules.order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
@Data
@TableName("t_order_device_mapping")
public class OrderDeviceMappingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 设备id
	 */
	private Long deviceId;
	/**
	 * 订单id
	 */
	private Long orderId;

}
