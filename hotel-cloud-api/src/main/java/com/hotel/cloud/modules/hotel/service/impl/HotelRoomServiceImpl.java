package com.hotel.cloud.modules.hotel.service.impl;

import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelRoomDao;
import com.hotel.cloud.modules.hotel.entity.HotelRoomEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomService;
import org.springframework.transaction.annotation.Transactional;


@Service("hotelRoomService")
public class HotelRoomServiceImpl extends ServiceImpl<HotelRoomDao, HotelRoomEntity> implements HotelRoomService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String hotelId = (String) params.get("hotelId");
        String roomTypeId = (String) params.get("roomTypeId");
        IPage<HotelRoomEntity> page = this.page(
                new Query<HotelRoomEntity>().getPage(params),
                new QueryWrapper<HotelRoomEntity>()
                        .eq(ShiroUtils.isAgent(), "create_by", ShiroUtils.getLoginUser().getUsername())
                        .eq(StringUtils.isNotBlank(hotelId), "hotel_id", hotelId)
                        .eq(StringUtils.isNotBlank(roomTypeId), "room_type_id", roomTypeId)
                        .eq("flag", FlagEnum.OK.getCode())
                        .orderByDesc("create_time")
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void batchDelete(Long[] ids) {
        this.checkAuth(ids);
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        List<HotelRoomEntity> rooms = Arrays.stream(ids).map(id -> {
            HotelRoomEntity room = new HotelRoomEntity();
            room.setId(id);
            room.setFlag(FlagEnum.DELETE.getCode());
            room.setUpdateBy(loginUser.getUsername());
            return room;
        }).collect(Collectors.toList());
        this.updateBatchById(rooms);
    }

    @Override
    public void update(HotelRoomEntity hotelRoom) {
        Long[] ids = {hotelRoom.getId()};
        checkAuth(ids);
        this.updateById(hotelRoom);
    }

    @Override
    public void disable(DisableVo disableVo) {
        this.checkAuth(disableVo.getId());
        Integer status = disableVo.getStatus();
        List<HotelRoomEntity> rooms = Arrays.stream(disableVo.getId()).map(id -> {
            HotelRoomEntity hotel = new HotelRoomEntity();
            hotel.setId(id);
            hotel.setStatus(status);
            return hotel;
        }).collect(Collectors.toList());
        this.updateBatchById(rooms);
    }

    private void checkAuth(Long[] ids) {
        this.checkAuth(this.baseMapper.selectBatchIds(Arrays.asList(ids)));
    }

    private void checkAuth(List<HotelRoomEntity> rooms) {
        boolean isAgent = ShiroUtils.isAgent();
        if (isAgent) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            for (HotelRoomEntity room : rooms) {
                if (!room.getCreateBy().equals(loginUser.getUsername())) {
                    throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
                }
            }
        }
    }

}