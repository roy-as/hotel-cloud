/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.hotel.cloud.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.cloud.common.enums.AgentTypeEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.FlagEnum;
import com.hotel.cloud.common.enums.UserTypeEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.modules.org.entity.AgentEntity;
import com.hotel.cloud.modules.org.service.AgentService;
import com.hotel.cloud.modules.sys.dao.SysUserDao;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.SysRoleService;
import com.hotel.cloud.modules.sys.service.SysUserRoleService;
import com.hotel.cloud.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private AgentService agentService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String username = (String) params.get("username");
        String name = (String) params.get("name");
        Long createUserId = (Long) params.get("createUserId");
        String orgName = (String) params.get("orgName");

        IPage<SysUserEntity> page = this.page(
                new Query<SysUserEntity>().getPage(params),
                new QueryWrapper<SysUserEntity>()
                        .like(StringUtils.isNotBlank(username), "username", username)
                        .like(StringUtils.isNotBlank(name), "name", name)
                        .eq(createUserId != null, "create_user_id", createUserId)
                        .like(StringUtils.isNotBlank(orgName), "org_name", orgName)
                        .eq("flag", FlagEnum.OK.getCode())
        );

        return new PageUtils(page);
    }

    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    public SysUserEntity queryByUserName(String username) {
        return baseMapper.queryByUserName(username);
    }

    @Override
    @Transactional
    public SysUserEntity saveUser(SysUserEntity user) {
        user.setCreateTime(new Date());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
        user.setSalt(salt);
        Integer userType = user.getUserType();
        if (userType.equals(UserTypeEnum.CHILD_USER.getLevel())) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            AgentEntity agent = agentService.getById(loginUser.getOrgId());
            user.setOrgId(agent.getId());
            user.setOrgName(agent.getName());
        }
        this.save(user);

        //检查角色是否越权
        checkRole(user);
        // 当用户不是系统用户时
        if (!userType.equals(UserTypeEnum.SYSTEM_USER.getLevel())) {
            List<Long> roleIdList = new ArrayList<>();
            switch (userType) {
                case 1:
                    if (ShiroUtils.isSysUser()) {// 系统用户添加时，默认是省级代理角色
                        roleIdList.add(Long.valueOf(AgentTypeEnum.PROVINCE_AGENT.getLevel()));
                    } else {
                        SysUserEntity loginUser = ShiroUtils.getLoginUser();
                        AgentEntity agent = agentService.getById(loginUser.getOrgId());
                        roleIdList.add(Long.valueOf(agent.getAgentLevel()) + 1);
                    }
                    break;
                case 2:
                    roleIdList.add(Constants.HOTEL_ROLE_ID);
                    break;
                case 3:
                    roleIdList.add(Constants.INSTALLATION_ROLE_ID);
                    break;
                case 4:
                    break;
                default:
                    throw new RRException(ExceptionEnum.ENUM_NOT_EXIST);

            }
            user.setRoleIdList(roleIdList);
        }
        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
        return user;
    }

    @Override
    @Transactional
    public void update(SysUserEntity user) {
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(null);
        } else {
            user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
        }
        this.updateById(user);

        //检查角色是否越权
        checkRole(user);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
    }

    @Override
    @Transactional
    public void deleteBatch(Long[] userIds) {
        List<SysUserEntity> users = Arrays.stream(userIds).map(userId -> {
            SysUserEntity user = new SysUserEntity();
            user.setUserId(userId);
            user.setFlag(FlagEnum.DELETE.getCode());
            return user;
        }).collect(Collectors.toList());
        this.updateBatchById(users);
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

    /**
     * 检查角色是否越权
     */
    private void checkRole(SysUserEntity user) {
        if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
            return;
        }
        if (!AgentTypeEnum.ALL_AGENT_LEVEL.containsAll(user.getRoleIdList())) {
            return;
        }
        //如果不是超级管理员，则需要判断用户的角色是否自己创建
        if (user.getCreateUserId() == Constants.SUPER_ADMIN) {
            return;
        }

        //查询用户创建的角色列表
        List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

        //判断是否越权
        if (!roleIdList.containsAll(user.getRoleIdList())) {
            throw new RRException("新增用户所选角色，不是本人创建");
        }
    }
}