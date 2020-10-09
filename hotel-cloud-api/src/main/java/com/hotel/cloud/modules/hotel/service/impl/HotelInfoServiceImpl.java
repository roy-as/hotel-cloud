package com.hotel.cloud.modules.hotel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hotel.cloud.common.enums.AgentLevelEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.enums.HotelPictureTypeEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.*;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.common.vo.HotelInfoVo;
import com.hotel.cloud.modules.hotel.entity.HotelOssMappingEntity;
import com.hotel.cloud.modules.hotel.service.HotelOssMappingService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.hotel.dao.HotelInfoDao;
import com.hotel.cloud.modules.hotel.entity.HotelInfoEntity;
import com.hotel.cloud.modules.hotel.service.HotelInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Service("hotelInfoService")
public class HotelInfoServiceImpl extends ServiceImpl<HotelInfoDao, HotelInfoEntity> implements HotelInfoService {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private HotelOssMappingService hotelOssMappingService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HotelInfoEntity> page = new Query<HotelInfoEntity>().getPage(params);
        List<HotelInfoEntity> list = this.baseMapper.getHotelInfoList(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveHotel(HotelInfoVo vo) throws IOException {
        HotelInfoEntity entity = vo.getHotelInfoEntity();
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        entity.setCreateBy(loginUser.getUsername());
        entity.setUpdateBy(loginUser.getUsername());
        entity.setCreateTime(new Date());
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
        MultipartFile[] fullViews = vo.getFullViews();
        if (CommonUtils.isNotEmpty(fullViews)) {
            ossList.addAll(
                    this.saveFile(entity.getId(), fullViews, HotelPictureTypeEnum.HOTEL_FULL_VIEW_PICTURE.getType())
            );
        }
        // 存储酒店图片
        MultipartFile[] hotelPictures = vo.getHotelPictures();
        if (CommonUtils.isNotEmpty(hotelPictures)) {
            ossList.addAll(
                    this.saveFile(entity.getId(), hotelPictures, HotelPictureTypeEnum.HOTEL_PICTURE.getType())
            );
        }
        if (CollectionUtil.isNotEmpty(ossList)) {
            this.hotelOssMappingService.saveBatch(ossList);
        }
    }

    /**
     * 保存文件
     * @param hotelId
     * @param file
     * @param pictureType
     * @return
     * @throws IOException
     */
    private HotelOssMappingEntity saveFile(Long hotelId, MultipartFile file, Integer pictureType) throws IOException {
        SysOssEntity oss = this.sysOssService.saveFile(file);
        return new HotelOssMappingEntity(
                hotelId, oss.getId(), pictureType
        );
    }

    /**
     * 保存文件
     * @param hotelId
     * @param files
     * @param pictureType
     * @return
     * @throws IOException
     */
    private List<HotelOssMappingEntity> saveFile(Long hotelId, MultipartFile[] files, Integer pictureType) throws IOException {
        List<HotelOssMappingEntity> ossList = new ArrayList<>(files.length);
        for(MultipartFile file : files) {
            HotelOssMappingEntity mappingEntity = this.saveFile(hotelId, file, pictureType);
            ossList.add(mappingEntity);
        }

        return ossList;
    }

    @Override
    @Transactional
    public void update(HotelInfoVo vo) throws IOException {
        checkAuth(vo.getId());
        HotelInfoEntity entity = vo.getHotelInfoEntity();
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        entity.setUpdateBy(loginUser.getUsername());
        this.updateById(entity);
        // 存储文件
        if (null != vo.getLogo()) {
            HotelOssMappingEntity mappingEntity = this.saveFile(
                    entity.getId(), vo.getLogo(), HotelPictureTypeEnum.HOTEL_LOGO.getType()
            );
            UpdateWrapper<HotelOssMappingEntity> wrapper = new UpdateWrapper<HotelOssMappingEntity>()
                    .eq(true, "hotel_id", mappingEntity.getHotelId())
                    .eq(true, "picture_type", mappingEntity.getPictureType());
            this.hotelOssMappingService.update(mappingEntity, wrapper);
        }

    }

    @Override
    public HotelInfoEntity getInfo(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        List<HotelInfoEntity> hotelInfoList = this.baseMapper.getHotelInfoList(null, params);
        return CollectionUtil.isNotEmpty(hotelInfoList) ? hotelInfoList.get(0) : null;
    }

    /**
     * 检查是否能修改
     *
     * @param id
     */
    private void checkAuth(Long id) {
        HotelInfoEntity hotel = this.getById(id);
        String createBy = hotel.getCreateBy();
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        Integer agentLevel = loginUser.getAgentLevel();
        if (!AgentLevelEnum.SYSTEM_USER.getLevel().equals(agentLevel)) {
            if (!createBy.equals(loginUser.getUsername())) {
                throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
            }
        }
    }

    @Override
    @Transactional
    public void disable(DisableVo vo) {
        Integer status = vo.getStatus();
        List<HotelInfoEntity> users = Arrays.stream(vo.getId()).map(id -> {
            HotelInfoEntity hotel = new HotelInfoEntity();
            hotel.setId(id);
            hotel.setStatus(status);
            return hotel;
        }).collect(Collectors.toList());
        this.updateBatchById(users);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        List<HotelInfoEntity> hotels = ids.stream().map(id -> {
            HotelInfoEntity hotel = new HotelInfoEntity();
            hotel.setId(id);
            hotel.setFlag(FlagEnum.DELETE.getCode());
            return hotel;
        }).collect(Collectors.toList());
        this.updateBatchById(hotels);
    }

}