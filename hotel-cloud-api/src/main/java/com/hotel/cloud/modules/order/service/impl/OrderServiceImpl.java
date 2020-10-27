package com.hotel.cloud.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.EquipStatusEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.R;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.DeviceService;
import com.hotel.cloud.modules.equipment.service.EquipModuleService;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.order.dao.OrderDao;
import com.hotel.cloud.modules.order.entity.OrderEntity;
import com.hotel.cloud.modules.order.service.OrderService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private EquipService equipService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private EquipModuleService equipModuleService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveOrder(OrderEntity order) {
        this.save(order);
    }

    @Override
    public R equipList() {
        List<EquipEntity> equips = equipService.list(new QueryWrapper<EquipEntity>()
                .eq("flag", FlagEnum.OK.getCode())
                .eq("status", EquipStatusEnum.PENDING_RELEASE.getCode())
                .orderByDesc("create_time")
        );

        Set<Long> moduleIds = equips.stream().map(EquipEntity::getModuleId).collect(Collectors.toSet());

        List<EquipModuleEntity> modules = equipModuleService.getBaseMapper().selectBatchIds(moduleIds);
        Map<Long, EquipModuleEntity> moduleMap = modules.stream().collect(Collectors.toMap(EquipModuleEntity::getId, module -> module));
        for(EquipEntity equip: equips) {
            equip.setPrice(moduleMap.get(equip.getModuleId()).getPrice());
        }

        List<DeviceEntity> devices = deviceService.list(new QueryWrapper<DeviceEntity>()
                .eq("flag", FlagEnum.OK.getCode())
                .gt("amount", Constants.ZERO)
                .orderByDesc("create_time")
        );
        return R.ok().put("equip", equips).put("device", devices);
    }

}