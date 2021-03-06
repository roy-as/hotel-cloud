package com.hotel.cloud.modules.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.hotel.RoomTypeVo;
import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;

import java.util.List;
import java.util.Map;

/**
 * 酒店房型表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-09 17:24:56
 */
public interface HotelRoomTypeService extends IService<HotelRoomTypeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveRoomType(RoomTypeVo vo);

    List<HotelRoomTypeEntity> select();

    void delete(List<Long> ids);

    void update(RoomTypeVo vo);

    void deletePicture(RoomTypeVo vo);
}

