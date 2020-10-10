package com.hotel.cloud.modules.equipment.service.impl;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.modules.equipment.dao.EquipModuleDao;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.EquipModuleService;


@Service("equipModuleService")
public class EquipModuleServiceImpl extends ServiceImpl<EquipModuleDao, EquipModuleEntity> implements EquipModuleService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EquipModuleEntity> page = this.page(
                new Query<EquipModuleEntity>().getPage(params),
                new QueryWrapper<EquipModuleEntity>()
        );

        return new PageUtils(page);
    }

}