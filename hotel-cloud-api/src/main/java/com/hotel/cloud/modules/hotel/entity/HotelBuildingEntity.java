package com.hotel.cloud.modules.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 酒店楼栋表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@Data
@TableName("t_hotel_building")
public class HotelBuildingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 楼栋名称
	 */
	private String name;
	/**
	 * 楼栋号
	 */
	private Integer number;
	/**
	 * 备注
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
