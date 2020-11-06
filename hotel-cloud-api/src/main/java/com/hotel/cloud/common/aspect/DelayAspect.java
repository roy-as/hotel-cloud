package com.hotel.cloud.common.aspect;

import com.hotel.cloud.common.annotation.Delay;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.common.utils.Constants;
import com.hotel.cloud.common.utils.IPUtils;
import com.hotel.cloud.common.utils.RedisUtils;
import com.hotel.cloud.common.vo.agent.RequestVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class DelayAspect {

    @Resource
    private RedisUtils redisUtils;

    @Pointcut("@annotation(com.hotel.cloud.common.annotation.Delay)")
    public void delayPointCut() {

    }

    @Around("delayPointCut()")
    public Object delay(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            throw new RRException(ExceptionEnum.REQUEST_FAIL);
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 获取ip
        String ip = IPUtils.getIpAddr(request);
        String uri = request.getRequestURI();
        String key = MessageFormat.format(Constants.REQUST_REDIS_KEY, ip, uri);
        // 获取请求信息
        RequestVo requestVo = redisUtils.get(key, RequestVo.class);

        // 获取注解
        Class<?> clazz = point.getTarget().getClass();
        Delay annotation = clazz.getAnnotation(Delay.class);
        long now = System.currentTimeMillis();
        if (null == annotation) {// 类上没有注解从方法上获取
            String methodName = point.getSignature().getName();
            Method method = clazz.getDeclaredMethod(methodName,
                    Arrays.stream(point.getArgs()).map(Object::getClass).toArray(Class[]::new));
            annotation = method.getAnnotation(Delay.class);
            Optional.ofNullable(annotation).orElseThrow(new RRException(ExceptionEnum.REQUEST_FAIL));
        }
        if (null != requestVo) { // 当请求不为空时
            int maxCount = annotation.maxCount();
            if (maxCount > requestVo.getCount()) {
                requestVo.setCount(requestVo.getCount() + 1);
                requestVo.setLastRequest(now);
                redisUtils.setRange(key, requestVo, 0L);
            } else {
                throw new RRException(ExceptionEnum.REQUEST_FREQUENT);
            }
        } else {
            requestVo = new RequestVo(ip, 1, uri, now);
            redisUtils.set(key, requestVo, annotation.time());
        }
        return point.proceed();
    }
}
