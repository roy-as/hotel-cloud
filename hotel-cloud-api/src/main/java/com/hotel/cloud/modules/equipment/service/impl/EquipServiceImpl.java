package com.hotel.cloud.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.EquipStatusEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.*;
import com.hotel.cloud.common.vo.equip.EquipVo;
import com.hotel.cloud.common.vo.equip.QrcodeVo;
import com.hotel.cloud.modules.equipment.dao.EquipDao;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;


@Service("equipService")
public class EquipServiceImpl extends ServiceImpl<EquipDao, EquipEntity> implements EquipService {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String mac = (String) params.get("mac");
        String moduleId = (String) params.get("moduleId");
        String hotelId = (String) params.get("hotelId");
        String agentName = (String) params.get("agentName");
        String expiredTimeFrom = (String) params.get("expiredTimeFrom");
        String expiredTimeEnd = (String) params.get("expiredTimeEnd");
        Date start = StringUtils.isNotBlank(expiredTimeFrom) ? new Date(Long.valueOf(expiredTimeFrom)) : null;
        Date end = StringUtils.isNotBlank(expiredTimeEnd) ? new Date(Long.valueOf(expiredTimeEnd)) : null;
        boolean isAgent = ShiroUtils.isAgent();
        String status = (String) params.get("status");
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        List<Long> agents = new ArrayList<>();
        if (isAgent) {
            agents.add(loginUser.getUserId());
            List<SysUserEntity> users = sysUserService.list(
                    new QueryWrapper<SysUserEntity>().eq("parent_id", loginUser.getUserId())
            );
            for (SysUserEntity user : users) {
                agents.add(user.getUserId());
            }
        }
        IPage<EquipEntity> page = this.page(
                new Query<EquipEntity>().getPage(params),
                new QueryWrapper<EquipEntity>()
                        .like(StringUtils.isNotBlank(mac), "mac", mac)
                        .eq(StringUtils.isNotBlank(moduleId), "module_id", moduleId)
                        .eq(StringUtils.isNotBlank(hotelId), "hotel_id", hotelId)
                        .in(isAgent, "agent_id", agents)
                        .like(StringUtils.isNotBlank(agentName), "agent_name", agentName)
                        .eq(StringUtils.isNotBlank(status), "status", status)
                        .ge(null != start, "expired_time", start)
                        .le(null != end, "expired_time", end)
                        .eq("flag", FlagEnum.OK.getCode())
                        .orderByDesc("create_time")
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void generateQrcode(QrcodeVo vo) throws IOException {
        Long id = vo.getId();
        EquipEntity equip = this.baseMapper.selectById(id);
        // 校验是否有设备二维码已生成
        if (StringUtils.isNotBlank(equip.getQrcodeInfo()) && null != equip.getOssId()) {
            throw new RRException(ExceptionEnum.EQUIP_HAVE_QRCODE);
        }
        byte[] qrCode = CommonUtils.createQrCode(vo.getInfo());
        SysOssEntity oss = sysOssService.saveFile(qrCode, Constants.PNG_SUFFIX);

        equip.setId(id);
        equip.setQrcodeInfo(vo.getInfo());
        equip.setOssId(oss.getId());
        equip.setQrcodeUrl(oss.getUrl());
        equip.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
        equip.setStatus(EquipStatusEnum.PENDING_OLD.getCode());


        this.updateById(equip);
    }

    @Override
    @Transactional
    public void releaseVo(EquipVo vo) {
        if (null == vo.getAgentId() && null == vo.getHotelId()) {
            throw new RRException("代理或者酒店不能为空");
        }
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(vo.getIds());
        Long hotelId = vo.getHotelId();
        Long agentId = vo.getAgentId();
        for (EquipEntity equip : equips) {
            this.checkEquipOwner(equip.getAgentId());
            if (null != agentId) {
                equip.setAgentId(agentId);
                equip.setAgentName(vo.getAgentName());
            }
            if (null != hotelId) {
                equip.setHotelId(hotelId);
                equip.setHotelName(vo.getHotelName());
                equip.setStatus(EquipStatusEnum.PENDING_SET.getCode());
            }
            equip.setUpdateBy(ShiroUtils.getLoginUser().getUsername());
            equip.setUpdateTime(new Date());
            equip.setExpiredTime(vo.getExpiredTime());
        }
        this.updateBatchById(equips);
    }

    @Override
    @Transactional
    public void recycle(Long[] ids) {
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(Arrays.asList(ids));
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        for (EquipEntity equip : equips) {
            equip.setStatus(EquipStatusEnum.RECYCLE.getCode());
            equip.setUpdateBy(loginUser.getUsername());
            equip.setHotelId(null);
            equip.setHotelName(null);
            equip.setAgentId(null);
            equip.setAgentName(null);
        }
        this.baseMapper.updateBatchById(equips);
    }

    @Override
    @Transactional
    public void old(List<Long> ids, Long count) {
        List<EquipEntity> equips = this.getBaseMapper().selectBatchIds(ids);
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        for (EquipEntity equip : equips) {
            equip.setStatus(EquipStatusEnum.PENDING_RELEASE.getCode());
            equip.setUpdateBy(loginUser.getUsername());
        }
        this.updateBatchById(equips);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(ids);
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        for (EquipEntity equip : equips) {
            if (null != equip.getHotelId()) {
                throw new RRException(ExceptionEnum.RELATE_HOTEL);
            }
            equip.setFlag(FlagEnum.DELETE.getCode());
            equip.setUpdateBy(loginUser.getUsername());
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
}