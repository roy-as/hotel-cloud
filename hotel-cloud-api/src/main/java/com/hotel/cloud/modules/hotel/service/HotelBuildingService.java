package com.hotel.cloud.modules.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.hotel.entity.HotelBuildingEntity;

import java.util.Map;

/**
 * 酒店楼栋表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
public interface HotelBuildingService extends IService<HotelBuildingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

