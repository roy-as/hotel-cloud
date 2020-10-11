package com.hotel.cloud.modules.equipment.service.impl;

import com.hotel.cloud.common.enums.AgentLevelEnum;
import com.hotel.cloud.common.enums.EquipStatusEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.*;
import com.hotel.cloud.common.vo.equip.QrcodeVo;
import com.hotel.cloud.common.vo.equip.ReleaseEquipVo;
import com.hotel.cloud.modules.agent.entity.AgentUserEntity;
import com.hotel.cloud.modules.agent.service.AgentUserService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hotel.cloud.modules.equipment.dao.EquipDao;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("equipService")
public class EquipServiceImpl extends ServiceImpl<EquipDao, EquipEntity> implements EquipService {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private AgentUserService agentUserService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        String moduleId = (String) params.get("moduleId");
        String hotelId = (String) params.get("hotelId");
        boolean isAgent = ShiroUtils.isAgent();
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        IPage<EquipEntity> page = this.page(
                new Query<EquipEntity>().getPage(params),
                new QueryWrapper<EquipEntity>()
                        .like(StringUtils.isNotBlank(name), "name", MessageFormat.format("%{0}%", name))
                        .eq(StringUtils.isNotBlank(moduleId), "module_id", moduleId)
                        .eq(StringUtils.isNotBlank(hotelId), "hotel_id", hotelId)
                        .eq(isAgent, "agent_id", loginUser.getUserId())
                        .orderByDesc("create_time")
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void generateQrcode(QrcodeVo vo) throws IOException {
        List<Long> ids = vo.getIds();
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(ids);
        // 校验是否有设备二维码已生成
        for (EquipEntity equip : equips) {
            if (StringUtils.isNotBlank(equip.getQrcodeInfo()) && null != equip.getOssId()) {
                throw new RRException(ExceptionEnum.EQUIP_HAVE_QRCODE);
            }
        }
        equips.clear();
        for (Long id : ids) {
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

    @Override
    @Transactional
    public void releaseVo(ReleaseEquipVo vo) {
        if (null == vo.getAgentId() && null == vo.getHotelId()) {
            throw new RRException("代理或者酒店不能为空");
        }
        // 检查下发到代理是否越权
        checkAuth(vo.getAgentId());
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(vo.getIds());
        Long hotelId = vo.getHotelId();
        Long agentId = vo.getAgentId();
        for (EquipEntity equip : equips) {
            this.checkEquipOwner(equip.getAgentId());
            if(null != agentId) {
                equip.setAgentId(agentId);
                equip.setAgentName(vo.getAgentName());
            }
            if(null != hotelId) {
                equip.setHotelId(hotelId);
                equip.setHotelName(vo.getHotelName());
                equip.setStatus(EquipStatusEnum.RELEASED.getCode());
            }
            equip.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
            equip.setUpdateTime(new Date());
        }
        this.updateBatchById(equips);
    }

    /**
     * 检查当前用户是否有权限下发设备
     *
     * @param agentId
     */
    private void checkEquipOwner(Long agentId) {
        if (ShiroUtils.isAgent()) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            if (!loginUser.getUserId().equals(agentId)) {
                throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
            }
        }
    }

    private void checkAuth(Long agentId) {
        if (null != agentId) {
            AgentUserEntity agentUser = agentUserService.getAgentUser(agentId);
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            Integer agentLevel = agentUser.getSysUser().getAgentLevel();
            if (!ShiroUtils.isAgent()) { // 当前用户是系统用户时只能下发到省级代理和区域代理
                if (!AgentLevelEnum.PROVINCE_AGENT.getLevel().equals(agentLevel)
                        && !AgentLevelEnum.INDIVIDUAL_AGENT.getLevel().equals(agentLevel)) {
                    throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
                }
            } else {// 当前用户为代理时，只能下发到下级代理
                // 下发设备到代理不能下发到不是自己的下级代理
                if (!agentUser.getSysUser().getParentId().equals(loginUser.getUserId())) {
                    throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
                }
                // 县级代理,区域代理不能下发到代理
                if (loginUser.getAgentLevel().equals(AgentLevelEnum.COUNTRY_AGENT.getLevel())
                        || loginUser.getAgentLevel().equals(AgentLevelEnum.INDIVIDUAL_AGENT.getLevel())) {
                    throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
                }
                // 当前代理只能下发到下级代理
                if (!agentLevel.equals(loginUser.getAgentLevel() + 1)) {
                    throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
                }
            }
        }
    }


    private String getEquipQrcodeInfoSuffix() {
        return UUID.randomUUID().toString().toUpperCase().substring(9, 24) + System.currentTimeMillis();
    }
}