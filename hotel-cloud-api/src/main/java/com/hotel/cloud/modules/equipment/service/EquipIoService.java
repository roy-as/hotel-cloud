package com.hotel.cloud.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.equipment.entity.EquipIoEntity;

import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-12 09:08:04
 */
public interface EquipIoService extends IService<EquipIoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

