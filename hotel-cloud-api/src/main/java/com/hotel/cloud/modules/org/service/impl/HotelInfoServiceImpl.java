package com.hotel.cloud.modules.org.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.enums.HotelPictureTypeEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.common.vo.hotel.HotelInfoVo;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.org.dao.HotelInfoDao;
import com.hotel.cloud.modules.org.entity.HotelInfoEntity;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.hotel.entity.HotelRoomTypeEntity;
import com.hotel.cloud.modules.hotel.service.HotelOssMappingService;
import com.hotel.cloud.modules.hotel.service.HotelRoomTypeService;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import com.hotel.cloud.modules.org.service.AgentService;
import com.hotel.cloud.modules.org.service.HotelInfoService;
import com.hotel.cloud.modules.org.service.OrgService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service("hotel")
public class HotelInfoServiceImpl extends ServiceImpl<HotelInfoDao, HotelInfoEntity> implements HotelInfoService, OrgService {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private HotelOssMappingService hotelOssMappingService;

    @Resource
    private HotelRoomTypeService hotelRoomTypeService;

    @Resource
    private EquipService equipService;

    @Resource
    private AgentService agentService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HotelInfoEntity> page = new Query<HotelInfoEntity>().getPage(params);
        if (ShiroUtils.isAgent()) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            params.put("createBy", loginUser.getUsername());
        }
        List<HotelInfoEntity> list = this.baseMapper.getHotelInfoList(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveHotel(HotelInfoVo vo) throws IOException {
        if(!ShiroUtils.isAgent()){
            throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
        }
        HotelInfoEntity entity = vo.getHotelInfoEntity();
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        AgentEntity agent = agentService.getById(loginUser.getOrgId());
        entity.setCreateBy(loginUser.getUsername());
        entity.setUpdateBy(loginUser.getUsername());
        entity.setCreateTime(new Date());
        entity.setAgentId(agent.getId());
        entity.setAgentName(agent.getName());
        // 存储酒店信息
        this.save(entity);
        List<HotelOssMappingEntity> ossList = new ArrayList<>();
        // 存储logo
        if (null != vo.getLogo()) {
            ossList.add(this.saveFile(
                    entity.getId(), vo.getLogo(), HotelPictureTypeEnum.HOTEL_LOGO.getType()
            ));
        }
        // 存错全景图
//        MultipartFile[] fullViews = vo.getFullViews();
//        if (CommonUtils.isNotEmpty(fullViews)) {
//            ossList.addAll(
//                    this.saveFile(entity.getId(), fullViews, HotelPictureTypeEnum.HOTEL_FULL_VIEW_PICTURE.getType())
//            );
//        }
//        // 存储酒店图片
//        MultipartFile[] hotelPictures = vo.getHotelPictures();
//        if (CommonUtils.isNotEmpty(hotelPictures)) {
//            ossList.addAll(
//                    this.saveFile(entity.getId(), hotelPictures, HotelPictureTypeEnum.HOTEL_PICTURE.getType())
//            );
//        }
        if (CollectionUtil.isNotEmpty(ossList)) {
            this.hotelOssMappingService.saveBatch(ossList);
        }
    }

    /**
     * 保存文件
     *
     * @param hotelId
     * @param file
     * @param pictureType
     * @return
     * @throws IOException
     */
    private HotelOssMappingEntity saveFile(Long hotelId, MultipartFile file, Integer pictureType) throws IOException {
        SysOssEntity oss = this.sysOssService.saveFile(file);
        return new HotelOssMappingEntity(
                hotelId, oss.getId(), pictureType, null
        );
    }

    /**
     * 保存文件
     *
     * @param hotelId
     * @param files
     * @param pictureType
     * @return
     * @throws IOException
     */
    private List<HotelOssMappingEntity> saveFile(Long hotelId, MultipartFile[] files, Integer pictureType) throws IOException {
        List<HotelOssMappingEntity> ossList = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            HotelOssMappingEntity mappingEntity = this.saveFile(hotelId, file, pictureType);
            ossList.add(mappingEntity);
        }

        return ossList;
    }

    @Override
    @Transactional
    public void update(HotelInfoVo vo) throws IOException {
        HotelInfoEntity entity = vo.getHotelInfoEntity();
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        entity.setUpdateBy(loginUser.getUsername());
        this.updateById(entity);
        List<HotelOssMappingEntity> ossList = new ArrayList<>();
        // 存储文件
        if (null != vo.getLogo()) {
            HotelOssMappingEntity mappingEntity = this.saveFile(
                    entity.getId(), vo.getLogo(), HotelPictureTypeEnum.HOTEL_LOGO.getType()
            );
            UpdateWrapper<HotelOssMappingEntity> wrapper = new UpdateWrapper<HotelOssMappingEntity>()
                    .eq(true, "hotel_id", mappingEntity.getHotelId())
                    .eq(true, "picture_type", mappingEntity.getPictureType());
            boolean success = this.hotelOssMappingService.update(mappingEntity, wrapper);
            if (!success) {
                this.hotelOssMappingService.insert(mappingEntity);
                ossList.add(mappingEntity);
            }
        }
        // 存错全景图
//        MultipartFile[] fullViews = vo.getFullViews();
//        if (CommonUtils.isNotEmpty(fullViews)) {
//            ossList.addAll(
//                    this.saveFile(entity.getId(), fullViews, HotelPictureTypeEnum.HOTEL_FULL_VIEW_PICTURE.getType())
//            );
//        }
//        // 存储酒店图片
//        MultipartFile[] hotelPictures = vo.getHotelPictures();
//        if (CommonUtils.isNotEmpty(hotelPictures)) {
//            ossList.addAll(
//                    this.saveFile(entity.getId(), hotelPictures, HotelPictureTypeEnum.HOTEL_PICTURE.getType())
//            );
//        }
        if (CollectionUtil.isNotEmpty(ossList)) {
            this.hotelOssMappingService.saveBatch(ossList);
        }

    }

    @Override
    public HotelInfoEntity getInfo(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        List<HotelInfoEntity> hotelInfoList = this.baseMapper.getHotelInfoList(null, params);
        return CollectionUtil.isNotEmpty(hotelInfoList) ? hotelInfoList.get(0) : null;
    }


    @Override
    @Transactional
    public void disable(DisableVo vo) {
        Integer status = vo.getStatus();
        List<HotelInfoEntity> hotels = Arrays.stream(vo.getId()).map(id -> {
            HotelInfoEntity hotel = new HotelInfoEntity();
            hotel.setId(id);
            hotel.setStatus(status);
            return hotel;
        }).collect(Collectors.toList());
        this.updateBatchById(hotels);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        List<Long> idLids = Arrays.asList(ids);
        boolean notEmpty = CollectionUtil.isNotEmpty(idLids);
        List<HotelRoomTypeEntity> roomTypes = hotelRoomTypeService.list(new QueryWrapper<HotelRoomTypeEntity>()
                .in(notEmpty, "hotel_id", idLids));
        if(CollectionUtil.isNotEmpty(roomTypes)) {
            throw new RRException(ExceptionEnum.EXIST_ROOM_TYPE);
        }
        List<EquipEntity> equips = equipService.list(new QueryWrapper<EquipEntity>()
                .in(notEmpty, "hotel_id", idLids));
        if(CollectionUtil.isNotEmpty(equips)) {
            throw new RRException(ExceptionEnum.EXIST_EQUIP);
        }
        List<HotelInfoEntity> hotels = idLids.stream().map(id -> {
            HotelInfoEntity hotel = new HotelInfoEntity();
            hotel.setId(id);
            hotel.setFlag(FlagEnum.DELETE.getCode());
            return hotel;
        }).collect(Collectors.toList());
        this.updateBatchById(hotels);
    }

    @Override
    public List<SysOssEntity> getPicture(HotelOssMappingEntity entity) {
        return this.baseMapper.getPicture(entity);
    }

    @Override
    public List<HotelInfoEntity> select() {
        boolean isAgent = ShiroUtils.isAgent();
        return this.list(
                new QueryWrapper<HotelInfoEntity>()
                        .eq(isAgent, "create_by", ShiroUtils.getLoginUser().getUsername())
                        .eq("status", FlagEnum.OK.getCode())
                        .eq("flag", FlagEnum.OK.getCode())
                        .orderByDesc("create_time")
        );
    }

    @Override
    @Transactional
    public void deletePicture(HotelInfoVo vo) {
        HotelInfoEntity hotel = this.getById(vo.getId());
        if (null != hotel) {
            hotelOssMappingService.getBaseMapper().delete(new QueryWrapper<HotelOssMappingEntity>()
                    .eq("hotel_id", vo.getId())
                    .in("oss_id", vo.getPictureIds())
            );
            sysOssService.removeByIds(vo.getPictureIds());
            hotel.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
            this.updateById(hotel);
        }
    }

}