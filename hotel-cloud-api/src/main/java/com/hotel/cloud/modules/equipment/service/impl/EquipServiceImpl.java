package com.hotel.cloud.modules.equipment.service.impl;

import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.CommonUtils;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.vo.QrcodeVo;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;

import com.hotel.cloud.modules.equipment.dao.EquipDao;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("equipService")
public class EquipServiceImpl extends ServiceImpl<EquipDao, EquipEntity> implements EquipService {

    @Resource
    private SysOssService sysOssService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EquipEntity> page = this.page(
                new Query<EquipEntity>().getPage(params),
                new QueryWrapper<EquipEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void generateQrcode(QrcodeVo vo) throws IOException {
        List<Long> ids = vo.getIds();
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(ids);
        // 校验是否有设备二维码已生成
        for(EquipEntity equip : equips) {
            if(StringUtils.isNotBlank(equip.getQrcodeInfo()) && null != equip.getOssId()) {
                throw new RRException(ExceptionEnum.EQUIP_HAVE_QRCODE);
            }
        }
        equips.clear();
        for(Long id : ids) {
            // 拼接二维码信息 前缀+起始值+随机12位UUID + 当前系统时间毫秒数
            String suffix = this.getEquipQrcodeInfoSuffix();
            String equipInfo = MessageFormat.format(
                    Constants.EQUIP_QRCODE_INFO, vo.getPrefix(), vo.getStart(), suffix
            );
            byte[] qrCode = CommonUtils.createQrCode(equipInfo);
            SysOssEntity oss = sysOssService.saveFile(qrCode, Constants.PNG_SUFFIX);
            EquipEntity equip = new EquipEntity();
            equip.setId(id);
            equip.setQrcodeInfo(equipInfo);
            equip.setOssId(oss.getId());
            equip.setQrcodeUrl(oss.getUrl());
            equips.add(equip);
        }
        this.updateBatchById(equips);
    }

    private String getEquipQrcodeInfoSuffix() {
        return UUID.randomUUID().toString().toUpperCase().substring(9, 24) + System.currentTimeMillis();
    }
}