package com.hotel.cloud.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.vo.equip.QrcodeVo;
import com.hotel.cloud.common.vo.equip.ReleaseEquipVo;
import com.hotel.cloud.modules.equipment.entity.EquipEntity;

import java.io.IOException;
import java.util.Map;

/**
 * 设备表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-10 11:22:30
 */
public interface EquipService extends IService<EquipEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void generateQrcode(QrcodeVo vo) throws IOException;

    void releaseVo(ReleaseEquipVo vo);
}

