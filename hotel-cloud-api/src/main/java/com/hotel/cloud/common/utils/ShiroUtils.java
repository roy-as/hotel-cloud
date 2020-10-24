package com.hotel.cloud.common.utils;

import com.hotel.cloud.common.enums.AgentTypeEnum;
import com.hotel.cloud.common.enums.ExceptionEnum;
import com.hotel.cloud.common.enums.UserTypeEnum;
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

	/**
	 * 获取当前登陆用户
	 * @return
	 */
	public static SysUserEntity getLoginUser() {
		Object user = SecurityUtils.getSubject().getPrincipal();
		if(null == user) {
			throw new RRException(ExceptionEnum.LOGIN_USER_EXPITED);
		}
		return (SysUserEntity) user;
	}

	/**
	 * 判断当前用户是否是代理
	 * @return
	 */
	public static boolean isAgent() {
		SysUserEntity loginUser = getLoginUser();
		Integer agentType = loginUser.getUserType();
		return UserTypeEnum.AGENT.getLevel().equals(agentType);
	}

	/**
	 * 判断登陆用户是否是系统用户
	 * @return
	 */
	public static boolean isSysUser() {
		SysUserEntity loginUser = getLoginUser();
		Integer agentType = loginUser.getUserType();
		return UserTypeEnum.SYSTEM_USER.getLevel().equals(agentType);
	}

}
