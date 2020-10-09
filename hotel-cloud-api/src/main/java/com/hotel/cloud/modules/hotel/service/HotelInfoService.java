package com.hotel.cloud.modules.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.common.vo.HotelInfoVo;
import com.hotel.cloud.modules.hotel.entity.HotelInfoEntity;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 酒店信息表
 * @date 2020-10-08 09:39:43
 */
public interface HotelInfoService extends IService<HotelInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveHotel(HotelInfoVo vo) throws IOException;

    void update(HotelInfoVo vo) throws IOException;

    HotelInfoEntity getInfo(Long id);

    void disable(DisableVo disableVo);

    void deleteBatch(List<Long> ids);

    List<SysOssEntity> getPicture(Long id, Integer pictureType);
}

