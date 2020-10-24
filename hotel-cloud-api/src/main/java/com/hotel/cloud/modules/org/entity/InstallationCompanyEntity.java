package com.hotel.cloud.modules.org.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 安装公司表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-24 09:07:56
 */
@Data
@TableName("t_installation_company")
public class InstallationCompanyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 安装公司名称
	 */
	private String name;
	/**
	 * 地区
	 */
	private String area;
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 　手机
	 */
	private String mobile;
	/**
	 *   联系人
	 */
	private String contact;
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
	 * 更新人时间
	 */
	private Date updateTime;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 标志位
	 */
	private Integer flag;

}
