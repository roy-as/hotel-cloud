package com.hotel.cloud.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.modules.order.dao.OrderDeviceMappingDao;
import com.hotel.cloud.modules.order.entity.OrderDeviceMappingEntity;
import com.hotel.cloud.modules.order.service.OrderDeviceMappingService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderDeviceMappingService")
public class OrderDeviceMappingServiceImpl extends ServiceImpl<OrderDeviceMappingDao, OrderDeviceMappingEntity> implements OrderDeviceMappingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderDeviceMappingEntity> page = this.page(
                new Query<OrderDeviceMappingEntity>().getPage(params),
                new QueryWrapper<OrderDeviceMappingEntity>()
        );

        return new PageUtils(page);
    }

}