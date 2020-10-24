package com.hotel.cloud.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.equip.DeviceVo;
import com.hotel.cloud.modules.equipment.dao.DeviceDao;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.DeviceService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.security.provider.SHA;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("deviceService")
public class DeviceServiceImpl extends ServiceImpl<DeviceDao, DeviceEntity> implements DeviceService {

    @Resource
    private SysOssService sysOssService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<DeviceEntity> page = this.page(
                new Query<DeviceEntity>().getPage(params),
                new QueryWrapper<DeviceEntity>()
                .eq("flag", FlagEnum.OK.getCode())
                .orderByDesc("create_time")
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void save(DeviceVo vo) throws IOException {
        DeviceEntity entity = vo.getEntity();
        if(null != vo.getPicture()) {
            this.saveFile(vo.getPicture(), entity);
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        entity.setCreateBy(loginUser.getUsername());
        entity.setUpdateBy(loginUser.getUsername());
        entity.setCreateTime(new Date());
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(DeviceVo vo) throws IOException {
        DeviceEntity entity = vo.getEntity();
        if(null != vo.getPicture()) {
            this.saveFile(vo.getPicture(), entity);
        }
        entity.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
        this.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        List<DeviceEntity> devices = ids.stream().map(id -> new DeviceEntity(id, FlagEnum.DELETE.getCode())).collect(Collectors.toList());
        this.updateBatchById(devices);
    }

    @Transactional
    public void saveFile(MultipartFile file, DeviceEntity entity) throws IOException {
        SysOssEntity oss = sysOssService.saveFile(file);
        entity.setOssId(oss.getId());
        entity.setPictureUrl(oss.getUrl());
    }

}