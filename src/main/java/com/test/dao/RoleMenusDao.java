package com.test.dao;

import com.test.entity.RolesMenus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * created by 李龙 on 2019-04-23
 **/
public interface RoleMenusDao {
    int deleteByRoleId(@Param("roleId") Long roleId);//根据roleId删除所有权限

    int insert(RolesMenus rolesMenus);//插入权限

    List<RolesMenus> selectRolesMenusByRoleId(@Param("roleId") Long roleId);//查询当前角色的菜单权限
}
