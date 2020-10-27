package com.hotel.cloud.modules.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.modules.org.dao.InstallationCompanyDao;
import com.hotel.cloud.modules.org.entity.InstallationCompanyEntity;
import com.hotel.cloud.modules.org.service.InstallationCompanyService;
import com.hotel.cloud.modules.org.service.OrgService;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("installation")
public class InstallationCompanyServiceImpl extends ServiceImpl<InstallationCompanyDao, InstallationCompanyEntity> implements InstallationCompanyService, OrgService<InstallationCompanyEntity> {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        String contact = (String) params.get("contact");
        IPage<InstallationCompanyEntity> page = this.page(
                new Query<InstallationCompanyEntity>().getPage(params),
                new QueryWrapper<InstallationCompanyEntity>()
                        .eq("flag", FlagEnum.OK.getCode())
                        .like(StringUtils.isNotBlank(name), "name", name)
                        .like(StringUtils.isNotBlank(contact), "contact", contact)
                        .orderByDesc("create_time")
        );
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        List<InstallationCompanyEntity> entities = ids.stream().map(id -> {
            InstallationCompanyEntity entity = new InstallationCompanyEntity();
            entity.setId(id);
            entity.setUpdateBy(loginUser.getUsername());
            entity.setFlag(FlagEnum.DELETE.getCode());
            return entity;
        }).collect(Collectors.toList());
        this.updateBatchById(entities);
    }

    @Override
    public List<InstallationCompanyEntity> select() {
        return this.list(new QueryWrapper<InstallationCompanyEntity>().orderByDesc("create_time"));
    }
}