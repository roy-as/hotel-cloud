package com.hotel.cloud.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
	private Long id;
	/**
	 * 设备模块名称
	 */
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
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 文件id
	 */
	private Long ossId;
	/**
	 * 图片地址
	 */
	private String pictureUrl;
	/**
	 * 协议
	 */
	private String protocol;

}
