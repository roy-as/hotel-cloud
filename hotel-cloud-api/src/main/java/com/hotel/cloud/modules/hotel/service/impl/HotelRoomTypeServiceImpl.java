package com.hotel.cloud.modules.hotel.service.impl;

import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.modules.hotel.entity.HotelInfoEntity;
import com.hotel.cloud.modules.hotel.service.HotelInfoService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelRoomTypeDao;
import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomTypeService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("hotelRoomTypeService")
public class HotelRoomTypeServiceImpl extends ServiceImpl<HotelRoomTypeDao, HotelRoomTypeEntity> implements HotelRoomTypeService {

    @Resource
    private HotelInfoService hotelInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String hotelId = (String) params.get("hotelId");
        IPage<HotelRoomTypeEntity> page = this.page(
                new Query<HotelRoomTypeEntity>().getPage(params),
                new QueryWrapper<HotelRoomTypeEntity>()
                .eq(StringUtils.isNotBlank(hotelId), "hotel_id", hotelId)
                .eq(ShiroUtils.isAgent(), "create_by", ShiroUtils.getLoginUser().getUsername())
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveRoomType(HotelRoomTypeEntity hotelRoomType) {
        Long hotelId = hotelRoomType.getHotelId();
        HotelInfoEntity hotel = hotelInfoService.getById(hotelId);
        if (null != hotel) {
            if(ShiroUtils.isAgent()) {
                this.checkCreateAuth(hotel);
            }
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            hotelRoomType.setCreateTime(new Date());
            hotelRoomType.setCreateBy(loginUser.getUsername());
            hotelRoomType.setUpdateBy(loginUser.getUsername());
            this.save(hotelRoomType);
        }
    }

    private void checkCreateAuth(HotelInfoEntity hotel) {
        this.checkCreateAuth(hotel.getCreateBy());
    }

    private void checkCreateAuth(String createBy) {
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        if (!loginUser.getUsername().equals(createBy)) {
            throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
        }
    }

}