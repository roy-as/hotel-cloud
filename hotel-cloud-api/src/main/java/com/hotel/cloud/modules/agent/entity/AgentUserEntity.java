package com.hotel.cloud.modules.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import lombok.Data;


@Data
@TableName("t_agent_user")
public class AgentUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统用户id
	 */
	@TableId(type = IdType.INPUT)
	private Long userId;
	/**
	 * 代理商名称
	 */
	private String agentName;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 地区
	 */
	private String area;

	@TableField(exist = false)
	private SysUserEntity sysUser;

	public static AgentUserEntity getSysUserInstance() {
		AgentUserEntity agentUser = new AgentUserEntity();
		agentUser.setUserId((long) Constants.ZERO);
		agentUser.setAgentName(Constants.NONE);
		SysUserEntity user = new SysUserEntity();
		user.setAgentLevelName(Constants.EMPTY);
		agentUser.setSysUser(user);
		return agentUser;
	}

}
