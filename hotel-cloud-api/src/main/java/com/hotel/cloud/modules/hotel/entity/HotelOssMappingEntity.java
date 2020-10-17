package com.hotel.cloud.modules.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.models.auth.In;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-08 14:48:15
 */
@Data
@TableName("t_hotel_oss_mapping")
@AllArgsConstructor
@NoArgsConstructor
public class HotelOssMappingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 酒店id
	 */
	private Long hotelId;
	/**
	 * 文件id
	 */
	private Long ossId;
	/**
	 * 照片类型，1:酒店实景图 2:酒店照片 3:房间实景图 4:房间图片
	 */
	private Integer pictureType;
	/**
	 * 房型id
	 */
	private Long roomTypeId;

	public HotelOssMappingEntity(Long hotelId, Integer pictureType) {
		this.hotelId = hotelId;
		this.pictureType = pictureType;
	}
}
