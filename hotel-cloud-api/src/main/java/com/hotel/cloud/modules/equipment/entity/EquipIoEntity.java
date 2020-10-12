package com.hotel.cloud.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-12 09:08:04
 */
@Data
@TableName("t_equip_io")
public class EquipIoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 设备IO名称
	 */
	private String name;
	/**
	 * 模块id
	 */
	private Long moduleId;
	/**
	 *  模块名称
	 */
	private String moduleName;
	/**
	 * IO类型，1:强电，2:弱电
	 */
	private Integer ioType;
	/**
	 * 开关类型，1:继电器，2:按键
	 */
	private Integer switchType;
	/**
	 *  按键类型
	 */
	private Long keyType;
	/**
	 * 按键名称
	 */
	private String keyName;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 更新人
	 */
	private String updateBy;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
}
