package com.test.dao;

import com.test.entity.Roles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * created by 李龙 on 2019-04-23
 **/
public interface RoleDao {
    List<Roles> listRole();//获取所有role角色
    Roles getRoleById(@Param("roleId") Long roleId);//获取当前roleid对应的角色
    int updateRole(Roles role);//更新角色信息
    Roles checkRoleName(@Param("roleName") String roleName);//根据角色名查询角色是否有重复
    int insert(Roles role);
    int deleteRoleById(@Param("roleId")Long roleId);//删除指定角色信息
}
