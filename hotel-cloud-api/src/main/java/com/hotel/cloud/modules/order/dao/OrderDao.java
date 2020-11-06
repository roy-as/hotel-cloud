package com.hotel.cloud.modules.order.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    List<DeviceEntity> deviceList(IPage<OrderEntity> page, Map<String, Object> params);
}
