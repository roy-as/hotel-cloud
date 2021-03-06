package com.hotel.cloud.modules.equipment.service.impl;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.modules.equipment.dao.EquipIoDao;
import com.hotel.cloud.modules.equipment.entity.EquipIoEntity;
import com.hotel.cloud.modules.equipment.service.EquipIoService;


@Service("equipIoService")
public class EquipIoServiceImpl extends ServiceImpl<EquipIoDao, EquipIoEntity> implements EquipIoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String ioType = (String) params.get("ioType");
        IPage<EquipIoEntity> page = this.page(
                new Query<EquipIoEntity>().getPage(params),
                new QueryWrapper<EquipIoEntity>()
                .eq(StringUtils.isNotBlank(ioType), "io_type", ioType)
        );

        return new PageUtils(page);
    }

}