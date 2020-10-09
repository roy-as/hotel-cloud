package com.hotel.cloud.modules.hotel.service.impl;

import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelRoomDao;
import com.hotel.cloud.modules.hotel.entity.HotelRoomEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomService;


@Service("hotelRoomService")
public class HotelRoomServiceImpl extends ServiceImpl<HotelRoomDao, HotelRoomEntity> implements HotelRoomService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HotelRoomEntity> page = this.page(
                new Query<HotelRoomEntity>().getPage(params),
                new QueryWrapper<HotelRoomEntity>()
        );

        return new PageUtils(page);
    }

}