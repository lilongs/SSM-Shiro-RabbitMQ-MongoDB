package com.test.dao;

import com.test.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * created by 李龙 on 2019-04-22
 **/
public interface MenusDao {
    List<Menu> getMenus(@Param("roleId") Long roleId);//根据roleid查询对应的菜单信息
    Menu getMenuById(@Param("menuId") Long menuId);//根据menuId获取菜单信息
    Menu checkMenuByTitle(@Param("title")String title);//检测菜单是否重复
    List<Menu> selectMenuList();//获取菜单列表
    List<Menu> checkMenuTitleSameLevel(Menu menus);//同级菜单名不相同
    int updateMenu(Menu menus);//更新菜单
    int insertMenu(Menu menus);//新增菜单
    int deleleteMenuById(@Param("menuId")Long menuId);
    List<Menu>  getMenuByParentId(@Param("parentId") Long parentId);//查询当前菜单是否有子菜单
}
