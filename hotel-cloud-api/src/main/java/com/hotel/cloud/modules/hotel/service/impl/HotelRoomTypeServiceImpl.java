package com.hotel.cloud.modules.hotel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.enums.HotelPictureTypeEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.hotel.RoomTypeVo;
import com.hotel.cloud.modules.hotel.entity.HotelInfoEntity;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.hotel.entity.HotelRoomEntity;
import com.hotel.cloud.modules.hotel.service.HotelInfoService;
import com.hotel.cloud.modules.hotel.service.HotelOssMappingService;
import com.hotel.cloud.modules.hotel.service.HotelRoomService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelRoomTypeDao;
import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;
import com.hotel.cloud.modules.hotel.service.HotelRoomTypeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Service("hotelRoomTypeService")
public class HotelRoomTypeServiceImpl extends ServiceImpl<HotelRoomTypeDao, HotelRoomTypeEntity> implements HotelRoomTypeService {

    @Resource
    private HotelInfoService hotelInfoService;

    @Resource
    private HotelRoomService hotelRoomService;

    @Resource
    private HotelOssMappingService hotelOssMappingService;

    @Resource
    private SysOssService sysOssService;

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
    public void saveRoomType(RoomTypeVo vo) {
        Long hotelId = vo.getHotelId();
        HotelInfoEntity hotel = hotelInfoService.getById(hotelId);
        if (null != hotel) {
            if (ShiroUtils.isAgent()) {
                this.checkCreateAuth(hotel);
            }
            HotelRoomTypeEntity hotelRoomType = vo.getHotelRoomTypeEntity();
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            hotelRoomType.setCreateTime(new Date());
            hotelRoomType.setCreateBy(loginUser.getUsername());
            hotelRoomType.setUpdateBy(loginUser.getUsername());
            this.save(hotelRoomType);
        }
    }

    @Override
    public List<HotelRoomTypeEntity> select() {
        boolean isAgent = ShiroUtils.isAgent();
        return this.list(new QueryWrapper<HotelRoomTypeEntity>()
                .eq(isAgent, "create_by", ShiroUtils.getLoginUser().getUsername())
                .orderByDesc("create_time"));
    }

    @Override
    public void delete(List<Long> ids) {
        List<HotelRoomEntity> rooms = hotelRoomService.list(new QueryWrapper<HotelRoomEntity>()
                .in(CollectionUtil.isNotEmpty(ids), "room_type_id", ids)
        );
        if(CollectionUtil.isNotEmpty(rooms)) {
            throw new RRException(ExceptionEnum.EXIST_ROOM);
        }
        this.removeByIds(ids);
    }

    @Override
    @Transactional
    public void update(RoomTypeVo vo) {
        checkAuth(vo.getId());
        HotelRoomTypeEntity entity = vo.getHotelRoomTypeEntity();
        entity.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
        this.updateById(entity);
        MultipartFile[] roomPictures = vo.getRoomPictures();
        if(CommonUtils.isNotEmpty(roomPictures)) {
            List<HotelOssMappingEntity> ossMapping = Arrays.stream(roomPictures).map(picture -> {
                try {
                    SysOssEntity oss = this.sysOssService.saveFile(picture);
                    return new HotelOssMappingEntity(
                            null, oss.getId(), HotelPictureTypeEnum.ROOM_PICTURE.getType(), vo.getId()
                    );
                } catch (Exception e) {
                    return null;
                }
            }).collect(Collectors.toList());
            this.hotelOssMappingService.saveBatch(ossMapping);
        }
    }

    @Override
    @Transactional
    public void deletePicture(RoomTypeVo vo) {
        HotelRoomTypeEntity entity = this.getById(vo.getId());
        if (null != entity) {
            checkAuth(entity);
            hotelOssMappingService.getBaseMapper().delete(new QueryWrapper<HotelOssMappingEntity>()
                    .eq("room_type_id", vo.getId())
                    .in("oss_id", vo.getPictureIds())
            );
            sysOssService.removeByIds(vo.getPictureIds());
            entity.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
            this.updateById(entity);
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

    private void checkAuth(HotelRoomTypeEntity entity) {
        if(ShiroUtils.isAgent()) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            if (!loginUser.getUsername().equals(entity.getCreateBy())) {
                throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
            }
        }
    }

    private void checkAuth(Long id) {
        if(ShiroUtils.isAgent()) {
            HotelRoomTypeEntity entity = this.getById(id);
            checkAuth(entity);
        }
    }
}