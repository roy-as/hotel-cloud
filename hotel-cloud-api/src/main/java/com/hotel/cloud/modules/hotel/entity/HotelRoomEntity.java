package com.hotel.cloud.modules.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 酒店房间表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@Data
@TableName("t_hotel_room")
public class HotelRoomEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 房间名称
	 */
	private String name;
	/**
	 * 房号
	 */
	private String number;
	/**
	 * 房间名称
	 */
	private String remark;
	/**
	 * 酒店ID
	 */
	private Long hotelId;
	/**
	 * 酒店ID
	 */
	private Long roomTypeId;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 删除标志位
	 */
	private Integer flag;
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
	/**
	 * 楼栋ID
	 */
	private Long buildingId;
	/**
	 * 楼层
	 */
	private int floor;

}
