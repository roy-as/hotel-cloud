package com.hotel.cloud.modules.oss.cloud;


import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.ConfigConstant;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.SpringContextUtils;
import com.hotel.cloud.modules.sys.service.SysConfigService;

/**
 * 文件上传Factory
 *
 */
public final class OSSFactory {
    private static SysConfigService sysConfigService;

    static {
        OSSFactory.sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
    }

    public static CloudStorageService build(){
        //获取云存储配置信息
        CloudStorageConfig config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorageConfig.class);

        if(config.getType() == Constants.CloudService.QINIU.getValue()){
            return new QiniuCloudStorageService(config);
        }else if(config.getType() == Constants.CloudService.ALIYUN.getValue()){
            return new AliyunCloudStorageService(config);
        }else if(config.getType() == Constants.CloudService.QCLOUD.getValue()){
            return new QcloudCloudStorageService(config);
        }
        throw new RRException(ExceptionEnum.OSS_CONFIG_NOT_EXIST);
    }

}
