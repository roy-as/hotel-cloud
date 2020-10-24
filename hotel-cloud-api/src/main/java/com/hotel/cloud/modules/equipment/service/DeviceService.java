package com.hotel.cloud.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.equip.DeviceVo;
import com.hotel.cloud.modules.equipment.entity.DeviceEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 智能设备
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-21 09:38:59
 */
public interface DeviceService extends IService<DeviceEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void save(DeviceVo vo) throws IOException;

    void update(DeviceVo vo) throws IOException;

    void delete(List<Long> ids);
}

