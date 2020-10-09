package com.hotel.cloud.modules.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;

import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-08 14:48:15
 */
public interface HotelOssMappingService extends IService<HotelOssMappingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void insert(HotelOssMappingEntity mappingEntity);
}

