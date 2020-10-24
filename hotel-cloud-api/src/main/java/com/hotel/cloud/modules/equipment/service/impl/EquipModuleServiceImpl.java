package com.hotel.cloud.modules.equipment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.equip.EquipModuleVo;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.modules.equipment.dao.EquipModuleDao;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.EquipModuleService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@Service("equipModuleService")
public class EquipModuleServiceImpl extends ServiceImpl<EquipModuleDao, EquipModuleEntity> implements EquipModuleService {

    @Resource
    private EquipService equipService;

    @Resource
    private SysOssService sysOssService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        IPage<EquipModuleEntity> page = this.page(
                new Query<EquipModuleEntity>().getPage(params),
                new QueryWrapper<EquipModuleEntity>()
                .like(StringUtils.isNotBlank(name), "name", MessageFormat.format("%{0}%", name))
        );

        return new PageUtils(page);
    }

    @Override
    public void delete(List<Long> ids) {
        List<EquipEntity> equips = equipService.list(new QueryWrapper<EquipEntity>()
                .in(CollectionUtil.isNotEmpty(ids), "module_id", ids)
        );
        if (CollectionUtil.isNotEmpty(equips)) {
            throw new RRException(ExceptionEnum.EXIST_EQUIP);
        }
        this.removeByIds(ids);
    }

    @Override
    @Transactional
    public void save(EquipModuleVo vo) throws IOException {
        EquipModuleEntity entity = vo.getEntity();
        if(null != vo.getPicture()) {
            saveFile(vo.getPicture(), entity);
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        entity.setCreateTime(new Date());
        entity.setCreateBy(loginUser.getUsername());
        entity.setUpdateBy(loginUser.getUsername());
        this.save(entity);
    }

    @Override
    public void update(EquipModuleVo vo) throws IOException {
        EquipModuleEntity entity = vo.getEntity();
        if(null != vo.getPicture()) {
            this.saveFile(vo.getPicture(), entity);
        }
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        entity.setUpdateBy(loginUser.getUsername());
        this.updateById(entity);
    }

    @Transactional
    public void saveFile(MultipartFile file, EquipModuleEntity entity) throws IOException {
        SysOssEntity oss = sysOssService.saveFile(file);
        entity.setOssId(oss.getId());
        entity.setPictureUrl(oss.getUrl());
    }

}