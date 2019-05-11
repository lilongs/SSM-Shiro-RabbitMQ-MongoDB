package com.test.servicesImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.test.dao.MenusDao;
import com.test.dao.RoleDao;
import com.test.dao.RoleMenusDao;
import com.test.dao.UserDao;
import com.test.entity.Menu;
import com.test.entity.Roles;
import com.test.entity.RolesMenus;
import com.test.entity.User;
import com.test.services.UserService;
import com.test.util.ResultUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * created by 李龙 on 2019-04-22
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MenusDao menusDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RoleMenusDao roleMenusDao;

    @Override
    public List<Menu> selfMenus(User admin) {
        List<Menu> results = new ArrayList<>();
        Long roleId = admin.getRoleId();
        List<Menu> menus = menusDao.getMenus(roleId);
        if (menus != null && menus.size() > 0) {
            for (int i = 0; i < menus.size(); i++) {
                if (menus.get(i).getParentId() == 0) {
                    Menu menu = new Menu();
                    menu.setTitle(menus.get(i).getTitle());
                    menu.setIcon(menus.get(i).getIcon());
                    menu.setHref(menus.get(i).getHref());
                    menu.setSpread(menus.get(i).getSpread());
                    List<Menu> menus2 = new ArrayList<>();
                    for (int j = 0; j < menus.size(); j++) {
                        if (menus.get(j).getParentId() == menus.get(i).getMenuId()) {
                            Menu menu2 = new Menu();
                            menu2.setTitle(menus.get(j).getTitle());
                            menu2.setIcon(menus.get(j).getIcon());
                            menu2.setHref(menus.get(j).getHref());
                            menu2.setSpread(menus.get(j).getSpread());
                            menus2.add(menu2);
                        }
                    }
                    menu.setChildren(menus2);
                    results.add(menu);
                }
            }
        }
        return results;
    }

    @Override
    public List<Roles> listRole(){
        return roleDao.listRole();
    }

    @Override
    public ResultUtil listRole(Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        List<Roles> list=roleDao.listRole();
        PageInfo<Roles> pageInfo = new PageInfo<Roles>(list);
        ResultUtil resultUtil = new ResultUtil();
        resultUtil.setCode(0);
        resultUtil.setCount(pageInfo.getTotal());
        resultUtil.setData(pageInfo.getList());
        return resultUtil;
    }

    @Override
    public ResultUtil userList(Integer page,Integer limit) {
        PageHelper.startPage(page, limit);
        List<User> list = userDao.userList();
        for (User tbAdmin : list) {
            // tbAdmin.setRoleName();
            List<Roles> roles = listRole();
            for (Roles tbRole : roles) {
                if (tbRole.getRoleId() == tbAdmin.getRoleId()) {
                    tbAdmin.setRoleName(tbRole.getRoleName());
                }
            }
        }
        PageInfo<User> pageInfo = new PageInfo<User>(list);
        ResultUtil resultUtil = new ResultUtil();
        resultUtil.setCode(0);
        resultUtil.setCount(pageInfo.getTotal());
        resultUtil.setData(pageInfo.getList());
        return resultUtil;
    }

    @Override
    public Roles getRole(Roles role) {
        return roleDao.getRoleById(role.getRoleId());
    }

    @Override
    public List<Menu> selXtreeData(Long roleId) {
        List<Menu> allMenus = menusDao.selectMenuList();
        if(!roleId.equals(Long.valueOf("-1"))){
            RolesMenus rolemenus = new RolesMenus();
            List<RolesMenus> roleMenus = roleMenusDao.selectRolesMenusByRoleId(roleId);
            for (Menu m : allMenus) {
                for (RolesMenus tbMenus : roleMenus) {
                    if (tbMenus.getMenuId() == m.getMenuId()) {
                        m.setChecked("true");
                    }
                }
            }
        }
        return allMenus;
    }

    @Override
    public void updRole(Roles role, String m) {
        // 更新角色信息
        roleDao.updateRole(role);
        // 先删除角色所有权限
        roleMenusDao.deleteByRoleId(role.getRoleId());
        if (m != null && m.length() != 0) {
            String[] result = m.split(",");
            // 重新赋予权限
            if (result != null && result.length > 0) {
                for (int i = 0; i < result.length; i++) {
                    RolesMenus record = new RolesMenus();
                    record.setRoleId(role.getRoleId());
                    record.setMenuId(Long.parseLong(result[i]));
                    // 维护角色、菜单表
                    roleMenusDao.insert(record);
                }
            }
        }
    }

    @Override
    public Roles checkRoleName(String roleName) {
        return roleDao.checkRoleName(roleName);
    }

    @Override
    public void insertRoleInfo(Roles role, String m) {
        // 维护角色表
        roleDao.insert(role);
        // 维护角色-菜单表
        if (m != null && m.length() != 0) {
            String[] result = m.split(",");
            if (result != null && result.length > 0) {
                for (int i = 0; i < result.length; i++) {
                    RolesMenus record = new RolesMenus();
                    record.setMenuId(Long.parseLong(result[i]));
                    record.setRoleId(role.getRoleId());
                    roleMenusDao.insert(record);
                }
            }
        }
    }

    @Override
    public int deleteRoleById(Long roleId) {
        return roleDao.deleteRoleById(roleId);
    }

    @Override
    public void deleteRoleByIds(String roleId) {
        String[] rids = roleId.split(",");
        for (String id : rids) {
            roleDao.deleteRoleById(Long.parseLong(id));
        }
    }

    @Override
    public void deleteUserById(Long Id) {
        userDao.deleteUserById(Id);
    }

    @Override
    public void deleteUserByIds(List<Long> Ids) {
        if(Ids!=null&&Ids.size()>0){
            for (Long id : Ids) {
                userDao.deleteUserById(id);
            }
        }
    }

    @Override
    public User checkByUserName(String username) {
        User user=new User();
        user.setUsername(username);
        List<User> userList=userDao.selectByUser(user);
        if(userList.size()!=0){
            return userList.get(0);
        }else {
            return null;
        }
    }

    @Override
    public Menu checkMenuByTitle(String title) {
        return menusDao.checkMenuByTitle(title);
    }

    @Override
    public int insertUser(User user) {
        user.setPassword(new Md5Hash(user.getPassword()).toString());
        return userDao.insertUser(user);
    }

    @Override
    public User selectUserById(Long Id) {
        List<Long> list=new ArrayList<>();
        list.add(Id);
        List<User> userList=userDao.selectUserById(list);
        if(userList.size()!=0) {
            return userList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public User checkUserEmail(String eMail) {
        User user=new User();
        user.seteMail(eMail);
        return userDao.selectByUser(user).get(0);
    }

    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public User checkUserPassword(String username, String password) {
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
       return userDao.selectByUser(user).get(0);
    }

    @Override
    public List<Menu> selectMenuList() {
        return menusDao.selectMenuList();
    }

    @Override
    public Menu getMenuById(Long menuId) {
        return menusDao.getMenuById(menuId);
    }

    @Override
    public List<Menu> checkMenuTitleSameLevel(Menu menus) {
        return menusDao.checkMenuTitleSameLevel(menus);
    }

    @Override
    public int updateMenu(Menu menus) {
        return menusDao.updateMenu(menus);
    }

    @Override
    public int insertMenu(Menu menus) {
        return menusDao.insertMenu(menus);
    }

    @Override
    public int deleleteMenuById(Long menuId) {
        return menusDao.deleleteMenuById(menuId);
    }

    @Override
    public List<Menu> getMenuByParentId(Long parentId) {
        return menusDao.getMenuByParentId(parentId);
    }
}
