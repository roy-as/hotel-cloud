package com.hotel.cloud.modules.agent.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableList;
import com.hotel.cloud.common.enums.AgentLevelEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.PageUtils;
import com.hotel.cloud.common.utils.Query;
import com.hotel.cloud.common.utils.ShiroUtils;
import com.hotel.cloud.common.vo.DisableVo;
import com.hotel.cloud.modules.agent.dao.AgentUserDao;
import com.hotel.cloud.modules.agent.entity.AgentUserEntity;
import com.hotel.cloud.modules.agent.service.AgentUserService;
import com.hotel.cloud.common.vo.AgentUserVo;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import com.hotel.cloud.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("agentUserService")
public class AgentUserServiceImpl extends ServiceImpl<AgentUserDao, AgentUserEntity> implements AgentUserService {

    @Resource
    private SysUserService sysUserService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AgentUserEntity> page = new Query<AgentUserEntity>().getPage(params);
        if (ShiroUtils.isAgent()) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            AgentUserEntity agentUser = this.getById(loginUser.getUserId());
            params.put("parentName", agentUser.getAgentName());
        }
        List<AgentUserEntity> list = this.baseMapper.getAgentUserList(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveVo(AgentUserVo vo) {
        SysUserEntity sysUserEntity = vo.getSysUserEntity();
        sysUserEntity.setCreateUserId(ShiroUtils.getUserId());
        Integer agentLevel = Optional.ofNullable(vo.getAgentLevel()).orElse(this.getAgentLevel(vo.getParentId()));
        sysUserEntity.setAgentLevel(agentLevel);
        Long roleId = (long) (agentLevel + 1);
        List<Long> roles = ImmutableList.of(roleId);
        sysUserEntity.setRoleIdList(roles);
        SysUserEntity user = this.sysUserService.saveUser(sysUserEntity);
        AgentUserEntity agentUserEntity = vo.getAgentUserEntity();
        agentUserEntity.setUserId(user.getUserId());
        this.save(agentUserEntity);
    }

    @Override
    public AgentUserEntity getAgentUser(Long userId) {
        return this.baseMapper.getAgentUser(userId);
    }

    @Override
    @Transactional
    public void updateAgentUser(AgentUserVo vo) {
        SysUserEntity sysUserEntity = vo.getSysUserEntity();
        AgentUserEntity agentUserEntity = vo.getAgentUserEntity();
        SysUserEntity user = sysUserService.getById(vo.getUserId());
        this.checkAuth(user);
        if (StringUtils.isNotBlank(vo.getPassword())) {
            sysUserEntity.setPassword(new Sha256Hash(vo.getPassword(), user.getSalt()).toHex());

        }
        this.sysUserService.updateById(sysUserEntity);
        this.updateById(agentUserEntity);
    }

    private void checkAuth(Long[] ids) {
        List<SysUserEntity> users = sysUserService.getBaseMapper().selectBatchIds(Arrays.asList(ids));
        for(SysUserEntity user : users) {
            checkAuth(user);
        }
    }

    private void checkAuth(SysUserEntity entity) {
        if (ShiroUtils.isAgent()) {
            SysUserEntity loginUser = ShiroUtils.getLoginUser();
            Long loginUserId = loginUser.getUserId();
            if (!entity.getCreateUserId().equals(loginUserId)
                    && !entity.getParentId().equals(loginUserId)) {
                throw new RRException(ExceptionEnum.NOT_AUTHENTICATION);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long[] userIds) {
        checkAuth(userIds);
        sysUserService.deleteBatch(userIds);
    }

    @Override
    @Transactional
    public void disable(DisableVo vo) {
        checkAuth(vo.getId());
        Integer status = vo.getStatus();
        List<SysUserEntity> users = Arrays.stream(vo.getId()).map(userId -> {
            SysUserEntity user = new SysUserEntity();
            user.setUserId(userId);
            user.setStatus(status);
            return user;
        }).collect(Collectors.toList());
        sysUserService.updateBatchById(users);
    }

    @Override
    public List<AgentUserEntity> select() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("limit", String.valueOf(Long.MAX_VALUE));
        params.put("agentSelect", true);
        SysUserEntity loginUser = ShiroUtils.getLoginUser();
        Integer agentLevel = loginUser.getAgentLevel();
        List<AgentUserEntity> list;
        if (AgentLevelEnum.SYSTEM_USER.getLevel().equals(agentLevel)) {
            list = new ArrayList<>();
            list.add(AgentUserEntity.getSysUserInstance());
            list.addAll((List<AgentUserEntity>) this.queryPage(params).getList());

        } else {
            AgentUserEntity agentUser = this.getAgentUser(loginUser.getUserId());
            list = ImmutableList.of(agentUser);
        }

        return list;
    }

    private Integer getAgentLevel(Long parentId) {
        Integer agentLevel = Constants.ZERO;
        if (Constants.ZERO != parentId) {
            SysUserEntity parent = sysUserService.getById(parentId);
            // 判断代理是否能创建下级子代理
            agentLevel = parent.getAgentLevel();
            if (agentLevel >= AgentLevelEnum.COUNTRY_AGENT.getLevel()) {
                throw new RRException(ExceptionEnum.NOT_ALLOW_CREATE_CHILD_AGENT);
            }
        }
        ++agentLevel;
        return agentLevel;
    }

}