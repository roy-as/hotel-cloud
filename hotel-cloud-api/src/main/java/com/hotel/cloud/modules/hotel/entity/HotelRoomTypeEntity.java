package com.hotel.cloud.modules.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 酒店房型表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@Data
@TableName("t_hotel_room_type")
public class HotelRoomTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 房型名称
	 */
	private String name;
	/**
	 * 面积
	 */
	private BigDecimal square;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 房型描述
	 */
	private String remark;
	/**
	 * 酒店ID
	 */
	private Long hotelId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 更新人
	 */
	private String updateBy;

}
