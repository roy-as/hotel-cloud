package com.hotel.cloud.common.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 常量
 */
public class Constants {
	/** 超级管理员ID */
	public static final int SUPER_ADMIN = 1;
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     * 0常量
     */
    public static final int ZERO = 0;
    /**
     * 无字符串
     */
    public static final String NONE = "无";
    /**
     * 空字符串
     */
    public static final String EMPTY = "";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    // 手机号正则
    public static final String MOBILE_PATTERN = "(13|14|15|16|17|18|19)[0-9]{9}";
    // 邮箱正则
    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$";
    // 默认字符编码
    public static final String DEFAULT_CHARSET = "UTF-8";
    // 二维码图片格式
    public static final String QRCODE_FORMAT = "PNG";
    public static final String PNG_SUFFIX = ".png";
    // 默认二维码宽
    public static final int DEFAULT_QRCODE_WIDTH = 240;
    // 默认二维码高度
    public static final int DEFAULT_QRCODE_HEIGHT = 240;
    // 酒店角色ID
    public static final Long HOTEL_ROLE_ID = 5L;
    // 安装公司角色ID
    public static final Long INSTALLATION_ROLE_ID = 6L;
    // 请求r信息edis的key
    public static final String REQUST_REDIS_KEY = "{0}:{1}";
    // LocalDate的格式化
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMM").withZone(ZoneId.systemDefault());

    public static final String HEX_PREFIX = "0x";
	/**
	 * 菜单类型
	 * 
	 * @author chenshun
	 * @date 2016年11月15日 下午1:24:29
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     * 
     * @author chenshun
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
