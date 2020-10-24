package com.hotel.cloud.modules.org.utils;

import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.UserTypeEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.modules.org.service.OrgService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrgServiceFactory {

    @Resource
    private Map<String, OrgService<?>> orgServiceMap = new ConcurrentHashMap<>();

    public OrgService<?> getInstance(Integer userType) {
        OrgService<?> service = orgServiceMap.get(UserTypeEnum.getDesc(userType));

        return Optional.ofNullable(service).orElseThrow(new RRException(ExceptionEnum.SERVICE_NOT_EXIST));
    }
}
