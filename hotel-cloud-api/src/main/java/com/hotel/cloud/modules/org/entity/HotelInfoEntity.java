package com.hotel.cloud.modules.org.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.hotel.cloud.modules.oss.entity.SysOssEntity;
import lombok.Data;

/**
 * 酒店信息表
 * @date 2020-10-08 09:39:43
 */
@Data
@TableName("t_hotel_info")
public class HotelInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 唯一主键
	 */
	@TableId
	private Long id;
	/**
	 * 酒店名称
	 */
	private String name;

	/**
	 * 地区
	 */
	private String area;
	/**
	 * 门店地址
	 */
	private String address;
	/**
	 * 酒店描述
	 */
	private String remark;
	/**
	 * 联系电话
	 */
	private String mobile;
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
	 * logo
	 */
	@TableField(exist = false)
	private SysOssEntity logo;
	/**
	 * 状态
	 */
	private Integer status;

	private Integer flag;
	/**
	 * 代理id
	 */
	private Long agentId;
	/**
	 * 代理名称
	 */
	private String agentName;

	private String contact;

}
