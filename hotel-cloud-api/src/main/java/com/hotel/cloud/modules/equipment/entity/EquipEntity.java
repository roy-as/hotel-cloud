package com.hotel.cloud.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 设备表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-10 11:22:30
 */
@Data
@TableName("t_equip")
public class EquipEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 设备名称
	 */
	private String name;
	/**
	 * 设备模块id
	 */
	private Long moduleId;
	/**
	 * 二维码ossId
	 */
	private Long ossId;
	/**
	 * 代理id
	 */
	private Long agentId;
	/**
	 * 酒店id
	 */
	private Long hotelId;
	/**
	 * 设备mac地址
	 */
	private String mac;
	/**
	 * 版本号
	 */
	private String versionNumber;
	/**
	 * 房间名称
	 */
	private String remark;
	/**
	 * 标志位
	 */
	private Integer flag;
	/**
	 * 状态
	 */
	private Integer status;
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
	 * 二维码信息
	 */
	private String qrcodeInfo;
	/**
	 * 模块名称
	 */
	private String moduleName;
	/**
	 * 酒店名称
	 */
	private String hotelName;
	/**
	 * 代理名称
	 */
	private String agentName;
	/**
	 * 序列号
	 */
	private String sn;
	/**
	 * 二维码地址
	 */
	private String qrcodeUrl;

}
