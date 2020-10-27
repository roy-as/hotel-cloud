package com.hotel.cloud.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.order.entity.OrderDeviceMappingEntity;

import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-27 09:22:31
 */
public interface OrderDeviceMappingService extends IService<OrderDeviceMappingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

