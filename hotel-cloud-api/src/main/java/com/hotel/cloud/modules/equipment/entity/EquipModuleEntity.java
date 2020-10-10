package com.hotel.cloud.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 设备模块表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-10 11:22:30
 */
@Data
@TableName("t_equip_module")
public class EquipModuleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 设备模块名称
	 */
	@NotBlank(message = "模块名称不能为空")
	private String name;
	/**
	 * 房间名称
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 更新人
	 */
	private String updateBy;

}
