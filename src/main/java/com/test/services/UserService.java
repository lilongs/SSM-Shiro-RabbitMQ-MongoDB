package com.test.services;

import com.test.entity.Menu;
import com.test.entity.Roles;
import com.test.entity.User;
import com.test.util.ResultUtil;

import java.util.List;

/**
 * created by 李龙 on 2019-04-22
 **/
public interface UserService {

    List<Menu> selfMenus(User admin);//获取用户对应的菜单

    List<Roles> listRole();//获取所有角色

    ResultUtil listRole(Integer page,Integer limit);//获取所有角色

    ResultUtil userList(Integer page, Integer limit);//用户列表

    Roles getRole(Roles role);//获取角色信息

    List<Menu> selXtreeData(Long roleId);//获取指定角色权限树

    void updRole(Roles role, String m);//更新角色信息

    Roles checkRoleName(String roleName);//根据角色名查询角色是否有重复

    void insertRoleInfo(Roles role, String m);//添加新角色信息

    int deleteRoleById(Long roleId);//删除指定角色信息

    void deleteRoleByIds(String roleId);//批量删除指定角色信息

    void deleteUserById(Long Id);//删除指定管理员

    void deleteUserByIds(List<Long> Id);//批量删除指定管理员

    User checkByUserName(String username);//检测用户名是否已存在

    Menu checkMenuByTitle(String title);//检测菜单是否重复

    int insertUser(User user);//新增用户

    User selectUserById(Long Id);//根据id查询用户信息

    User checkUserEmail(String eMail);//检测用户邮箱是否重复

    int updateUser(User user);//更新用户信息

    User checkUserPassword(String username,String password);//检测用户密码

    List<Menu> selectMenuList();//获取菜单列表

    Menu getMenuById(Long menuId);//根据meunId获取菜单信息

    List<Menu> checkMenuTitleSameLevel(Menu menus);//同级菜单名不相同

    int updateMenu(Menu menus);//更新菜单

    int insertMenu(Menu menus);//新增菜单

    int deleleteMenuById(Long menuId);//删除指定菜单

    List<Menu>  getMenuByParentId(Long parentId);//查询当前菜单是否有子菜单
}
