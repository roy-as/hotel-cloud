package com.hotel.cloud.modules.hotel.service.impl;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelBuildingDao;
import com.hotel.cloud.modules.hotel.entity.HotelBuildingEntity;
import com.hotel.cloud.modules.hotel.service.HotelBuildingService;


@Service("hotelBuildingService")
public class HotelBuildingServiceImpl extends ServiceImpl<HotelBuildingDao, HotelBuildingEntity> implements HotelBuildingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        String hotelName = (String) params.get("hotelName");
        IPage<HotelBuildingEntity> page = this.page(
                new Query<HotelBuildingEntity>().getPage(params),
                new QueryWrapper<HotelBuildingEntity>()
                .like(StringUtils.isNotBlank(name), "name", MessageFormat.format("%{0}%", name))
                .like(StringUtils.isNotBlank(hotelName), "hotel_name", MessageFormat.format("%{0}%", hotelName))
        );

        return new PageUtils(page);
    }

}