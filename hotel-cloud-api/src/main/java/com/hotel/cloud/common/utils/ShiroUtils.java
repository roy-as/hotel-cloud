package com.hotel.cloud.common.utils;

import com.hotel.cloud.common.enums.AgentLevelEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.exception.RRException;
import com.hotel.cloud.modules.sys.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * Shiro工具类
 */
public class ShiroUtils {

	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}


	public static Long getUserId() {
		return getLoginUser().getUserId();
	}
	
	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static Object getSessionAttribute(Object key) {
		return getSession().getAttribute(key);
	}

	public static boolean isLogin() {
		return SecurityUtils.getSubject().getPrincipal() != null;
	}

	public static String getKaptcha(String key) {
		Object kaptcha = getSessionAttribute(key);
		if(kaptcha == null){
			throw new RRException("验证码已失效");
		}
		getSession().removeAttribute(key);
		return kaptcha.toString();
	}

	public static SysUserEntity getLoginUser() {
		Object user = SecurityUtils.getSubject().getPrincipal();
		if(null == user) {
			throw new RRException(ExceptionEnum.LOGIN_USER_EXPITED);
		}
		return (SysUserEntity) user;
	}

	public static boolean isAgent() {
		SysUserEntity loginUser = getLoginUser();
		Integer agentLevel = loginUser.getAgentLevel();
		return !AgentLevelEnum.SYSTEM_USER.getLevel().equals(agentLevel);
	}

}
