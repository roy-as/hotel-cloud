package com.hotel.cloud.common.exception;

import com.hotel.cloud.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

/**
 * 异常处理器
 */
@RestControllerAdvice
public class RRExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RRException.class)
	public R handleRRException(RRException e){
		R r = new R();
		r.put("code", e.getCode());
		r.put("msg", e.getMessage());

		return r;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public R handlerNoFoundException(Exception e) {
		logger.error(e.getMessage(), e);
		return R.error(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public R handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return R.error("数据库中已存在该记录");
	}

	@ExceptionHandler(AuthorizationException.class)
	public R handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage(), e);
		return R.error("没有权限，请联系管理员授权");
	}

	@ExceptionHandler(Exception.class)
	public R handleException(Exception e){
		logger.error(e.getMessage(), e);
		return R.error();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public R handleConstraintViolationException(MethodArgumentNotValidException e) {
		logger.error(e.getMessage(), e);
		return R.error(this.getErrorMsg(e.getBindingResult().getFieldErrors()));
	}

	@ExceptionHandler(BindException.class)
	public R handleCBindException(BindException e) {
		logger.error(e.getMessage(), e);
		return R.error(this.getErrorMsg(e.getBindingResult().getFieldErrors()));
	}

	private String getErrorMsg(List<FieldError> fieldErrors) {
		StringBuilder errorMsg = new StringBuilder();
		for (FieldError error : fieldErrors) {
			String message = error.getDefaultMessage();
			if (StringUtils.isNotEmpty(message)) {
				errorMsg.append(message).append(";");
			}
		}
		return errorMsg.toString();
	}
}
