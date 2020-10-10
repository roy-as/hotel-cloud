package com.hotel.cloud.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;

import java.util.Map;

/**
 * 设备模块表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-10 11:22:30
 */
public interface EquipModuleService extends IService<EquipModuleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

