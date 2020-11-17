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
import com.hotel.cloud.config.property.SysProperty;
import com.hotel.cloud.modules.equipment.dao.EquipDao;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;
import com.hotel.cloud.modules.equipment.entity.EquipModuleEntity;
import com.hotel.cloud.modules.equipment.service.EquipModuleService;
import com.hotel.cloud.modules.equipment.service.EquipService;
import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import com.hotel.cloud.modules.oss.service.SysOssService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.MqttService;
import com.hotel.cloud.modules.sys.service.SysUserService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;


@Service("equipService")
public class EquipServiceImpl extends ServiceImpl<EquipDao, EquipEntity> implements EquipService {

    @Resource
    private SysOssService sysOssService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private EquipModuleService equipModuleService;

    @Resource
    private MqttService mqttService;

    @Resource
    private SysProperty sysProperty;

    @PostConstruct
    public void init() {
        String temDir = sysProperty.getTempDir();
        File file = new File(temDir);
        if (!file.exists() || file.isFile()) {
            file.mkdirs();
        }
    }

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

    @Override
    public List<EquipEntity> get(QueryWrapper<EquipEntity> wrapper) {
        List<EquipEntity> equips = this.list(wrapper);
        Set<Long> moduleIds = equips.stream().map(EquipEntity::getModuleId).collect(Collectors.toSet());
        List<EquipModuleEntity> modules = equipModuleService.getBaseMapper().selectBatchIds(moduleIds);
        Map<Long, EquipModuleEntity> moduleMap = modules.stream().collect(Collectors.toMap(EquipModuleEntity::getId, module -> module));
        for (EquipEntity equip : equips) {
            equip.setPrice(moduleMap.get(equip.getModuleId()).getPrice());
        }
        return equips;
    }

    @Override
    @Transactional
    public void saveEquip(EquipEntity equip) throws MqttException {
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        equip.setCreateTime(new Date());
        equip.setCreateBy(loginUser.getUsername());
        equip.setUpdateBy(loginUser.getUsername());
        mqttService.subscribe(equip.getMac());
    }

    @Override
    public void download(Long[] ids, HttpServletResponse response) throws IOException {
        List<EquipEntity> equips = this.baseMapper.selectBatchIds(Arrays.asList(ids));
        List<Callable<InputStream>> tasks = new ArrayList<>(equips.size());
        for (EquipEntity equip : equips) {
            tasks.add(() -> HttpUtils.get(equip.getQrcodeUrl()));
        }
        List<InputStream> isList = ThreadPoolUtils.execute(tasks);
        Set<String> filePathList = new LinkedHashSet<>(isList.size());
        for (InputStream is : isList) {
            String filePath = sysProperty.getTempDir() + CommonUtils.uuid() + Constants.PNG_SUFFIX;
            FileOutputStream os = new FileOutputStream(new File(filePath));
            IOUtils.copy(is, os);
            os.close();
            is.close();
            filePathList.add(filePath);
        }
        String fileName = LocalDateTime.now().format(Constants.FORMATTER_TIME) + Constants.ZIP_SUFFIX;
        CommonUtils.download(response, fileName);
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        CommonUtils.zip(filePathList, zos);
    }

    @Override
    @Transactional
    public void importExcel(MultipartFile file) throws Exception {
        List<EquipEntity> equips = ExcelUtils.importExcel(file, 0, 1, EquipEntity.class);

        Map<String, List<EquipEntity>> maps = equips.stream().map(equip -> {
            if (StringUtils.isBlank(equip.getModuleName())) {
                throw new RRException("设备模块不能为空");
            }
            if (StringUtils.isBlank(equip.getName())) {
                throw new RRException("设备名称不能为空");
            }
            if (StringUtils.isBlank(equip.getMac())) {
                throw new RRException("mac地址不能为空");
            }
            return equip;
        }).collect(Collectors.groupingBy(EquipEntity::getModuleName));
        Set<String> moduleNames = maps.keySet();
        List<EquipModuleEntity> modules = equipModuleService.list(new QueryWrapper<EquipModuleEntity>().in("name", moduleNames));
        if (modules.size() != moduleNames.size()) {
            throw new RRException(ExceptionEnum.EQUIP_MODULE_NOT_EXIST);
        }
        Map<String, Long> moduleMap = modules.stream().collect(Collectors.toMap(EquipModuleEntity::getName, EquipModuleEntity::getId));
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        for (EquipEntity equip : equips) {
            equip.setModuleId(moduleMap.get(equip.getModuleName()));
            equip.setCreateBy(loginUser.getUsername());
            equip.setUpdateBy(loginUser.getUsername());
            equip.setCreateTime(new Date());
            mqttService.subscribe(equip.getMac());
        }
        this.saveBatch(equips);

        List<QrcodeVo> qrcodeVos = equips.stream().filter(equipEntity -> StringUtils.isNotBlank(equipEntity.getQrcodeInfo()))
                .map(equip -> new QrcodeVo(equip.getId(), equip.getQrcodeInfo())).collect(Collectors.toList());

        this.generateQrcode(qrcodeVos);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateQrcode(List<QrcodeVo> qrcodeVos) {
        try{
            for (QrcodeVo qrcodeVo : qrcodeVos) {
                this.generateQrcode(qrcodeVo);
            }
        }catch (Exception e) {
            log.error("绑定二维码信息失败", e);
        }
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(Constants.EQUIP_IMPORT_TEMPLATE);
        OutputStream os = response.getOutputStream();
        CommonUtils.download(response, Constants.EQUIP_TEMPLATE_FILE_NAME);
        IOUtils.copy(is, os);
        os.flush();
        is.close();
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