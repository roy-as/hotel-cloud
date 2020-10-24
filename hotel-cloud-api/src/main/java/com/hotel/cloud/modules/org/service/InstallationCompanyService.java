package com.hotel.cloud.modules.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.modules.org.entity.InstallationCompanyEntity;

import java.util.List;
import java.util.Map;

/**
 * 安装公司表
 *
 * @author ${author}
 * @email ${email}
 * @date 2020-10-24 09:07:56
 */
public interface InstallationCompanyService extends IService<InstallationCompanyEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteBatch(List<Long> ids);
}

