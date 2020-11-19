package com.hotel.cloud.modules.sys.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * @date 2020-11-18 19:55:16
 */
@Data
@TableName("t_qrcode")
public class QrcodeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 二维码信息
	 */
	@Excel(name = "二维码信息")
	private String info;
	/**
	 * 地址
	 */
	private String url;
	/**
	 * 设备id
	 */
	private Long equipId;
	/**
	 * 设备名称
	 */
	private String equipName;
	/**
	 * 创建人
	 */
	private String createBy;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
