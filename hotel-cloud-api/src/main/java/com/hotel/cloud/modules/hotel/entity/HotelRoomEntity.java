package com.hotel.cloud.modules.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
	@NotBlank(message = "房间名不能为空")
	private String name;
	/**
	 * 房号
	 */
	@NotNull(message = "房号不能为空")
	private String number;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 酒店ID
	 */
	@NotNull(message = "酒店不能为空")
	private Long hotelId;
	/**
	 * 酒店ID
	 */
	@NotNull(message = "房型不能为空")
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
	 * 楼层
	 */
	@NotNull(message = "楼层不能为空")
	private int floor;
	/**
	 * 酒店名称
	 */
	private String hotelName;
	/**
	 * 房型名称
	 */
	private String roomTypeName;

}
