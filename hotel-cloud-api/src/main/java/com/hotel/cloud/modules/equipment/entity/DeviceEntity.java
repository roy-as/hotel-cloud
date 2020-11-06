package com.hotel.cloud.modules.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 智能设备
 * 
 * @author ${author}
 * @email ${email}
 * @date 2020-10-21 09:38:59
 */
@Data
@TableName("t_device")
@NoArgsConstructor
public class DeviceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 文件id
	 */
	private Long ossId;
	/**
	 * 图片url
	 */
	private String pictureUrl;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 描述
	 */
	private String remark;
	/**
	 * 当前数量
	 */
	private Integer amount;
	/**
	 * 销售数量
	 */
	private Integer saleAmount;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 更新人
	 */
	private String updateBy;
	/**
	 * 标志位
	 */
	private Integer flag;

	@TableField(exist = false)
	private Integer shopNumber = 0;

	@TableField(exist = false)
	private Integer maxNumber;

	@TableField(exist = false)
	private Integer deviceType;

	public void setAmount(Integer amount) {
		this.amount = amount;
		this.maxNumber = amount;
	}

	public DeviceEntity(Long id, Integer flag) {
		this.id = id;
		this.flag = flag;
	}
}
