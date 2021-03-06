package com.hotel.cloud.modules.org.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.cloud.modules.org.entity.HotelInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 酒店信息表
 * @date 2020-10-08 09:39:43
 */
@Mapper
public interface HotelInfoDao extends BaseMapper<HotelInfoEntity> {


    List<HotelInfoEntity> getHotelInfoList(IPage<HotelInfoEntity> page, Map<String, Object> params);

    List<SysOssEntity> getPicture(HotelOssMappingEntity entity);
}
