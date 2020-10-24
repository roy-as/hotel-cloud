package com.hotel.cloud.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.equip.EquipModuleVo;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;

import java.io.IOException;
import java.util.List;
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

    void delete(List<Long> ids);

    void save(EquipModuleVo vo) throws IOException;

    void update(EquipModuleVo vo) throws IOException;
}

