package com.hotel.cloud.modules.org.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hotel.cloud.common.enums.AgentTypeEnum;
import com.hotel.cloud.common.utils.Constants;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("t_agent")
public class AgentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统用户id
	 */
	@TableId
	private Long id;
	/**
	 * 代理商名称
	 */
	private String name;
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

	String contact;

	private String mobile;

	private String email;

	private Integer agentLevel;

	private Long parentId;

	private String parentName;

	private String createBy;

	private String updateBy;

	private Date createTime;

	private Date updateTime;

	private Integer status;

	private Integer flag;

	@TableField(exist = false)
	private String agentLevelName;

	public void setAgentLevel(Integer agentLevel) {
		this.agentLevel = agentLevel;
		this.agentLevelName = AgentTypeEnum.getAgentLevelName(agentLevel);
	}

	public static AgentEntity getEmptyEntity() {
		AgentEntity entity = new AgentEntity();
		entity.setId((long) Constants.ZERO);
		entity.setName(Constants.NONE);
		entity.setAgentLevelName(Constants.EMPTY);
		return entity;
	}
}
