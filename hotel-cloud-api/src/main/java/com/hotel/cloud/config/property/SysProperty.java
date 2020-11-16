package com.hotel.cloud.config.property;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.sys")
public class SysProperty {

    private String tempDir;

    public void setTempDir(String  tempDir) {
        if(StringUtils.isNotBlank(tempDir) && !tempDir.endsWith("/") && !tempDir.endsWith("\\")) {
            if(tempDir.contains("/") && !tempDir.contains("\\")) {
                this.tempDir = tempDir + "/";
            } else if (!tempDir.contains("/") && tempDir.contains("\\")){
                this.tempDir = tempDir + "\\";
            } else {
                this.tempDir = tempDir.replaceAll("\\\\", "/") + "/";
            }
        }
    }
}
