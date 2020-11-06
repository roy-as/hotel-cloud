package com.hotel.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * 频率限制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delay {

	long time() default 1000L;

	int maxCount() default 5;
}
