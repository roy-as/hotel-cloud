package com.hotel.cloud.modules.equipment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.modules.equipment.dao.EquipModuleDao;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.EquipModuleService;

import javax.annotation.Resource;


@Service("equipModuleService")
public class EquipModuleServiceImpl extends ServiceImpl<EquipModuleDao, EquipModuleEntity> implements EquipModuleService {

    @Resource
    private EquipService equipService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EquipModuleEntity> page = this.page(
                new Query<EquipModuleEntity>().getPage(params),
                new QueryWrapper<EquipModuleEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void delete(List<Long> ids) {
        List<EquipEntity> equips = equipService.list(new QueryWrapper<EquipEntity>()
                .in(CollectionUtil.isNotEmpty(ids), "module_id", ids)
        );
        if(CollectionUtil.isNotEmpty(equips)) {
            throw new RRException(ExceptionEnum.EXIST_EQUIP);
        }
        this.removeByIds(ids);
    }

}