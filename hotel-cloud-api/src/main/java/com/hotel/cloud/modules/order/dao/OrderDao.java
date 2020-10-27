package com.hotel.cloud.modules.order.dao;

import com.hotel.cloud.modules.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
