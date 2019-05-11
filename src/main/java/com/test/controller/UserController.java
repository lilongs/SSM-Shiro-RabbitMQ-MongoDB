package com.test.controller;

import com.google.code.kaptcha.Producer;
import com.test.entity.Menu;
import com.test.entity.Roles;
import com.test.entity.User;
import com.test.services.UserService;
import com.test.shiro.ShiroUtils;
import com.test.util.JsonUtils;
import com.test.util.RRException;
import com.test.util.ResultUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * created by 李龙 on 2019-04-22
 **/
@Controller
@RequestMapping("sys")
public class UserController {
    @Autowired
    private Producer captchaProducer = null;
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param vcode
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ResultUtil login(String username, String password, String vcode) {
        if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)||StringUtils.isEmpty(vcode)){
            throw new RRException("参数不能为空");
        }
        if(!vcode.toLowerCase().equals(ShiroUtils.getKaptcha("kaptcha").toLowerCase())){
            return ResultUtil.error("验证码不正确");
        }
        try{
            Subject subject = ShiroUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            subject.login(token);
        }catch (UnknownAccountException e) {
            return ResultUtil.error(e.getMessage());
        }catch (IncorrectCredentialsException e) {
            return ResultUtil.error(e.getMessage());
        }catch (LockedAccountException e) {
            return ResultUtil.error(e.getMessage());
        }catch (AuthenticationException e) {
            return ResultUtil.error("账户验证失败");
        }
        return ResultUtil.ok();
    }

    /**
     * 用户退出
     * @return
     */
    @RequestMapping(value="/loginOut")
    public String loginOut(){
        ShiroUtils.logout();
        return "redirect:/selectByUser.jsp";
    }

    /**
     * 验证码
     * @param resp
     * @throws Exception
     */
    @RequestMapping("/vcode")
    public void vcode(HttpServletResponse resp) throws Exception {
        String text = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(text);
        ShiroUtils.setSessionAttribute("kaptcha", text);
        ImageIO.write(image, "JPEG", resp.getOutputStream());
    }

    @RequestMapping("/main")
    public String main() {
        return "redirect:/pages/welcome.html";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest req) {
        User admin = (User)SecurityUtils.getSubject().getPrincipal();
        req.setAttribute("admin", admin);
        //用户登录后，查询名下进行中的待办任务数量
        return "redirect:/index.jsp";
    }

    @RequestMapping(value = "/getMenus", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @ResponseBody
    public List<Menu> getMenus() {
        User admin = (User)SecurityUtils.getSubject().getPrincipal();
        List<Menu> menus = null;
        if (admin != null) {
            menus = userService.selfMenus(admin);
        }
        return menus;
    }

    @RequestMapping("/adminList")
    public String adminList() {
        return "page/admin/adminList";
    }

    @RequestMapping("/menuList")
    public String menuList() {
        return "page/admin/menuList";
    }

    @RequestMapping("/personalData")
    public String personalData(HttpServletRequest req) {
        User admin = (User)SecurityUtils.getSubject().getPrincipal();
        List<Roles> roles = userService.listRole();
        req.setAttribute("ad",admin);
        req.setAttribute("roles", roles);
        return "page/admin/personalData";
    }

    /**
     * 管理员列表
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("/getAdminList")
    @RequiresPermissions("sys:admin:list")
    @ResponseBody
    public ResultUtil getAdminList(Integer page,Integer limit) {
        ResultUtil admins = userService.userList(page, limit);
        return admins;
    }

    /**
     * 角色列表
     * @return
     */
    @RequestMapping("/roleList")
    @RequiresPermissions("sys:role:list")
    public String roleList() {
        return "page/admin/roleList";
    }

    /**
     * 管理员列表
     */
    @RequestMapping("/getRoleList")
    @RequiresPermissions("sys:role:list")
    @ResponseBody
    public ResultUtil getRoleList(Integer page,Integer limit) {
        return userService.listRole(page, limit);
    }

    /**
     * 跳转编辑角色页面
     */
    @RequestMapping("/editRole")
    @RequiresPermissions("sys:role:update")
    public String editRole(Roles role,Model model) {
        role=userService.getRole(role);
        model.addAttribute("role", role);
        return "page/admin/editRole";
    }

    /**
     * 得到指定角色权限树
     * @return
     */
    @RequestMapping(value="/xtreedata",produces = {"text/json;charset=UTF-8"})
    @ResponseBody
    public String xtreeData(@RequestParam(value="roleId", defaultValue="-1") Long roleId) {
        return JsonUtils.objectToJson(userService.selXtreeData(roleId));
    }

    /**
     * 更新角色信息
     */
//    @SysLog(value="更新角色信息")
    @RequestMapping("/updRole")
    @RequiresPermissions("sys:role:update")
    @ResponseBody
    public void updRole(Roles role,String m) {
        //角色信息保存
        userService.updRole(role, m);
    }

    /**
     * 添加新角色
     */
//    @SysLog(value="添加角色信息")
    @RequestMapping("/insRole")
    @RequiresPermissions("sys:role:save")
    @ResponseBody
    public ResultUtil insertRole(Roles role,String m) {
        Roles r = userService.checkRoleName(role.getRoleName());
        if(r!=null){
            return new ResultUtil(500, "角色名已存在,请重试！");
        }
        //角色信息保存
        userService.insertRoleInfo(role, m);
        return ResultUtil.ok();
    }

    /**
     * 删除指定角色信息
     */
//    @SysLog(value="删除指定角色信息")
    @RequestMapping("/delRole/{roleId}")
    @RequiresPermissions("sys:role:delete")
    @ResponseBody
    public ResultUtil delRole(@PathVariable("roleId")Long roleId) {
        ResultUtil resultUtil=new ResultUtil();
        try {
            userService.deleteRoleById(roleId);
            resultUtil.setCode(0);
        } catch (Exception e) {
            resultUtil.setCode(500);
            e.printStackTrace();
        }
        return resultUtil;
    }

    /**
     * 批量删除指定角色信息
     */
//    @SysLog(value="批量删除指定角色信息")
    @RequestMapping("/delRoles/{rolesId}")
    @RequiresPermissions("sys:role:delete")
    @ResponseBody
    public ResultUtil delRoles(@PathVariable("rolesId")String rolesId) {
        ResultUtil resultUtil=new ResultUtil();
        try {
            userService.deleteRoleByIds(rolesId);
            resultUtil.setCode(0);
        } catch (Exception e) {
            resultUtil.setCode(500);
            e.printStackTrace();
        }
        return resultUtil;
    }

    @RequestMapping("/addRole")
    @RequiresPermissions("sys:role:save")
    public String addRole() {
        return "page/admin/addRole";
    }

    /**
     * 角色名唯一性检查
     */
    @RequestMapping("/checkRoleName/{roleName}")
    @ResponseBody
    public ResultUtil checkRoleName(Long roleId, @PathVariable("roleName")String roleName) {
        Roles role = userService.checkRoleName(roleName);
        if(role==null){
            return new ResultUtil(0);
        }else if(role.getRoleId()==roleId){
            return new ResultUtil(0);
        }else{
            return new ResultUtil(500,"角色名已存在！");
        }
    }

    /**
     * 通过id删除用户
     */
//    @SysLog(value="删除指定用户")
    @RequestMapping("/delAdminById/{id}")
    @RequiresPermissions("sys:admin:delete")
    @ResponseBody
    public ResultUtil delAdminById(@PathVariable("id")Long id) {
        if(id==1){
            return ResultUtil.error();
        }
        try {
            userService.deleteUserById(id);
            return ResultUtil.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error();
        }
    }

    /**
     * 批量删除指定管理员
     */
//    @SysLog(value="批量删除指定管理员")
    @RequestMapping("/delAdmins/{adminStr}")
    @RequiresPermissions("sys:admin:delete")
    @ResponseBody
    public ResultUtil delAdmins(HttpServletRequest req,@PathVariable("adminStr")String adminStr) {
        String[] strs = adminStr.split(",");
        List<Long> adminList=new ArrayList<>();
        for (String str : strs) {
            User admin = (User)SecurityUtils.getSubject().getPrincipal();
            if((admin.getId()==Long.parseLong(str))){
                return ResultUtil.error();
            }
            if("1".equals(str)){
                return ResultUtil.error();
            }
            adminList.add(Long.parseLong(str));
        }
        try {
            userService.deleteUserByIds(adminList);
            return ResultUtil.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error();
        }
    }

    @RequestMapping("/addAdmin")
    @RequiresPermissions("sys:admin:save")
    public String addAdmin(HttpServletRequest req){
        List<Roles> roles = userService.listRole();
        req.setAttribute("roles", roles);
        return "page/admin/addAdmin";
    }

    /**
     * 管理员用户名唯一性检查
     */
    @RequestMapping("/checkAdminName/{username}")
    @ResponseBody
    public ResultUtil checkAdminName(@PathVariable("username")String username) {
        User admin = userService.checkByUserName(username);
        if(admin!=null){
            return new ResultUtil(500,"用户名已存在！");
        }
        return new ResultUtil(0);
    }

    /**
     * 菜单名唯一性校验
     */
    @RequestMapping("/checkMenuTitle/{title}")
    @ResponseBody
    public ResultUtil checkMenuTitle(@PathVariable("title")String title) {
        Menu menu = userService.checkMenuByTitle(title);
        if(menu!=null){
            return new ResultUtil(500,"菜单已存在！");
        }
        return new ResultUtil(0);
    }

    /**
     * 增加管理員
     * 日期类型会导致数据填充失败，请求没反应
     */
//    @SysLog(value="添加管理员")
    @RequestMapping("/insAdmin1")
    @RequiresPermissions("sys:admin:save")
    @ResponseBody
    public ResultUtil insAdmin(User admin) {
        //防止浏览器提交
        User a = userService.checkByUserName(admin.getUsername());
        if(a!=null){
            return new ResultUtil(500, "用户名已存在,请重试！");
        }
        userService.insertUser(admin);
        return ResultUtil.ok();
    }

    @RequestMapping("/editAdmin/{id}")
    @RequiresPermissions("sys:admin:update")
    public String editAdmin(HttpServletRequest req,@PathVariable("id")Long id) {
        User ad = userService.selectUserById(id);
        List<Roles> roles = userService.listRole();
        req.setAttribute("ad",ad);
        req.setAttribute("roles", roles);
        return "page/admin/editAdmin";
    }

    @RequestMapping("/checkAdminByEmail")
    @ResponseBody
    public ResultUtil checkAdminByEmail(String eMail,String username) {
        User admin=userService.checkUserEmail(eMail);
        if(admin!=null){
            return new ResultUtil(500,"邮箱已被占用！");
        }
        return new ResultUtil(0);
    }

    /**
     * 更新管理员信息
     */
//    @SysLog(value="更新管理员信息")
    @RequestMapping("/updAdmin")
    @RequiresPermissions("sys:admin:update")
    @ResponseBody
    public ResultUtil updAdmin(User admin) {
        if(admin!=null&&admin.getId()==1){
            return ResultUtil.error("不允许修改!");
        }
        try {
            userService.updateUser(admin);
            return ResultUtil.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error();
        }
    }

    @RequestMapping("/changePwd")
    public String changePwd() {
        return "page/admin/changePwd";
    }

    /**
     * 修改密码
     */
//    @SysLog(value="修改密码")
    @RequestMapping("/updPwd")
    @ResponseBody
    public ResultUtil updPwd(HttpServletRequest req,String oldPwd,String newPwd) {
        User user = (User)SecurityUtils.getSubject().getPrincipal();
        if(user!=null){
            //测试账号不支持修改密码
            if("test".equals(user.getUsername())){
                return ResultUtil.error();
            }
            User admin = userService.checkUserPassword(user.getUsername(), oldPwd);
            if(admin!=null){
                admin.setPassword(newPwd);
                userService.updateUser(admin);
                //修改密码后移除作用域，重新登陆
                SecurityUtils.getSubject().logout();
                return ResultUtil.ok();
            }else{
                return new ResultUtil(501,"旧密码错误，请重新填写！");
            }
        }
        return new ResultUtil(500,"请求错误！");
    }

    @RequestMapping("/druid")
    @RequiresPermissions("sys:druid:list")
    public String druid(){
        return "redirect:/druid/index.html";
    }

    /**
     * 获取菜单信息
     */
    @RequestMapping("/menuData")
    @RequiresPermissions("sys:menu:list")
    @ResponseBody
    public ResultUtil menuData(){
        List<Menu> list=userService.selectMenuList();
        ResultUtil resultUtil=new ResultUtil();
        resultUtil.setCode(0);
        resultUtil.setCount(list.size()+0L);
        resultUtil.setData(list);
        return resultUtil;
    }

    @RequestMapping("/toSaveMenu/{menuId}")
    @RequiresPermissions("sys:menu:save")
    public String toSaveMenu(@PathVariable("menuId") Long menuId,Model model){
        if(menuId!=null&&menuId!=1){
            Menu menus=new Menu();
            menus.setMenuId(menuId);
            model.addAttribute("menu",menus);
            model.addAttribute("flag","1");
            return "page/admin/menuForm";
        }else{
            model.addAttribute("msg","不允许操作！");
            return "page/active";
        }
    }
    @RequestMapping("/toEditMenu/{menuId}")
    @RequiresPermissions("sys:menu:update")
    public String toEditMenu(@PathVariable("menuId") Long menuId,Model model){
        if(menuId!=null&&menuId!=1){
            Menu menus=userService.getMenuById(menuId);
            model.addAttribute("menu",menus);
            return "page/admin/menuForm";
        }else if(menuId==1){
            model.addAttribute("msg","不允许操作此菜单！");
            return "page/active";
        }else{
            model.addAttribute("msg","不允许操作！");
            return "page/active";
        }
    }

//    @SysLog("维护菜单信息")
    @RequestMapping("/menuForm")
    @RequiresPermissions(value={"sys:menu:save","sys:menu:update"})
    @ResponseBody
    public ResultUtil menuForm(Menu menus,String flag){
        if(StringUtils.isEmpty(flag)){
            //同级菜单名不相同
            List<Menu> data=userService.checkMenuTitleSameLevel(menus);
            Menu m = userService.getMenuById(menus.getMenuId());
            Boolean f=false;
            if(m.getTitle().equals(menus.getTitle())||data.size()==0){
                f=true;
            }
            if(!f||data.size()>1){
                return ResultUtil.error("同级菜单名不能相同！");
            }
            menus.setSpread("false");
            userService.updateMenu(menus);
            return ResultUtil.ok("修改成功！");
        }else if(menus.getMenuId()!=1){
            menus.setParentId(menus.getMenuId());

            //规定只能3级菜单
            Menu m=userService.getMenuById(menus.getMenuId());
            if(m!=null&&m.getParentId()!=0){
                Menu m1=userService.getMenuById(m.getParentId());
                if(m1!=null&&m1.getParentId()!=0){
                    return ResultUtil.error("此菜单不允许添加子菜单！");
                }
            }

            //同级菜单名不相同
            List<Menu> data=userService.checkMenuTitleSameLevel(menus);
            if(data.size()>0){
                return ResultUtil.error("同级菜单名不能相同！");
            }

            menus.setMenuId(null);
            menus.setSpread("false");
            userService.insertMenu(menus);
            return ResultUtil.ok("添加成功！");
        }else{
            return ResultUtil.error("此菜单不允许操作！");
        }
    }

//    @SysLog(value="删除菜单信息")
    @RequestMapping("/delMenuById/{menuId}")
    @RequiresPermissions("sys:menu:delete")
    @ResponseBody
    public ResultUtil delMenuById(@PathVariable("menuId")Long menuId) {
        try {
            if(menuId==1){
                return ResultUtil.error("此菜单不允许删除！");
            }
            //查询是否有子菜单，不允许删除
            List<Menu> data=userService.getMenuByParentId(menuId);
            if(data!=null&&data.size()>0){
                return ResultUtil.error("包含子菜单，不允许删除！");
            }
            userService.deleleteMenuById(menuId);
            return ResultUtil.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("系统错误！");
        }
    }

    /**
     * 获取菜单信息
     */
    @RequestMapping("/updMenuSortingById")
    @RequiresPermissions("sys:menu:update")
    @ResponseBody
    public ResultUtil updMenuSortingById(Long menuId,String sorting){
        Menu menus=new Menu();
        menus.setMenuId(menuId);
        try{
            Long.parseLong(sorting);
        }catch(NumberFormatException e)
        {
            return ResultUtil.error("修改失败，只允许输入整数！");
        }
        menus.setSorting(Long.parseLong(sorting));
        userService.updateMenu(menus);
        return ResultUtil.ok();
    }

}
