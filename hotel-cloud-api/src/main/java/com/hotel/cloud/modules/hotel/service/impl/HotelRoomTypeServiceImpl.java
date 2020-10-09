package com.hotel.cloud.modules.hotel.service.impl;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelRoomTypeDao;
import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomTypeService;


@Service("hotelRoomTypeService")
public class HotelRoomTypeServiceImpl extends ServiceImpl<HotelRoomTypeDao, HotelRoomTypeEntity> implements HotelRoomTypeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HotelRoomTypeEntity> page = this.page(
                new Query<HotelRoomTypeEntity>().getPage(params),
                new QueryWrapper<HotelRoomTypeEntity>()
        );

        return new PageUtils(page);
    }

}