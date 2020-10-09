package com.hotel.cloud.modules.hotel.dao;

import com.hotel.cloud.modules.hotel.entity.HotelRoomEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒店房间表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
@Mapper
public interface HotelRoomDao extends BaseMapper<HotelRoomEntity> {
	
}
