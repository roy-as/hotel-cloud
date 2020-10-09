package com.hotel.cloud.modules.hotel.service.impl;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelOssMappingDao;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.hotel.service.HotelOssMappingService;
import org.springframework.transaction.annotation.Transactional;


@Service("hotelOssMappingService")
public class HotelOssMappingServiceImpl extends ServiceImpl<HotelOssMappingDao, HotelOssMappingEntity> implements HotelOssMappingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HotelOssMappingEntity> page = this.page(
                new Query<HotelOssMappingEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void insert(HotelOssMappingEntity mappingEntity) {
        this.baseMapper.save(mappingEntity);
    }

}