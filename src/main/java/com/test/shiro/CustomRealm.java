package com.test.shiro;

import com.test.dao.MenusDao;
import com.test.dao.UserDao;
import com.test.entity.Menu;
import com.test.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 
 * @Desc: 自定义Realm
 * @author Mr Du
 * @createTime 2018年2月25日
 * @version: v1.0
 */
public class CustomRealm extends AuthorizingRealm {
	@Autowired
	private UserDao userDao;
	@Autowired
	private MenusDao menusDao;
	
	private static Logger logger=LoggerFactory.getLogger(CustomRealm.class);

	public CustomRealm() {
		logger.info("CustomRealm====================");
	}

	@Override
	public String getName() {
		return "CustomRealm";
	}

	/**
	 * realm授权方法 从输入参数principalCollection得到身份信息 根据身份信息到数据库查找权限信息 将权限信息添加给授权信息对象
	 * 返回 授权信息对象(判断用户访问url是否在权限信息中没有体现)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		User user = (User) principalCollection.getPrimaryPrincipal();
		Long roleId = user.getRoleId();
		List<String> permsList = null;

		// 系统管理员，拥有最高权限
		List<Menu> menuList = menusDao.getMenus(roleId);
		permsList = new ArrayList<>(menuList.size());
		for (Menu menu : menuList) {
			if (menu.getPerms() != null && !"".equals(menu.getPerms())) {
				permsList.add(menu.getPerms());
			}
		}
		// 用户权限列表
		Set<String> permsSet = new HashSet<String>();
		for (String perms : permsList) {
			if (perms.length()==0||perms==null) {
				continue;
			}
			permsSet.addAll(Arrays.asList(perms.trim().split(",")));
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(permsSet);
		return info;
	}

	/**
	 * 表单认证过滤器认证时会调用自定义Realm的认证方法进行认证，成功回到index.do，再跳转到index.jsp页面
	 *
	 * 前提：表单认证过滤器收集和组织用户名和密码信息封装为token对象传递给此方法
	 *
	 * token:封装了身份信息和凭证信息 2步骤：比对身份 信息；比对凭证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		String password = new String((char[]) token.getCredentials());

		// 查询用户信息
		User user=new User();
		user.setUsername(username);
		List<User> users = userDao.selectByUser(user);
		// 账号不存在
		if (user == null || users.size() == 0) {
			throw new UnknownAccountException("账号不存在!");
		}
		password = new Md5Hash(password).toString();
		// 密码错误
		if (!password.equals(users.get(0).getPassword())) {
			throw new IncorrectCredentialsException("账号或密码不正确!");
		}
		// 账号未分配角色
		if (users.get(0).getRoleId() == null || users.get(0).getRoleId() == 0) {
			throw new UnknownAccountException("账号未分配角色!");
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(users.get(0), password, getName());
		return info;
	}
}