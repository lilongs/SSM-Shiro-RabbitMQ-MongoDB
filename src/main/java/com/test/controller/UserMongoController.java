package com.test.controller;

import com.test.entity.NewUser;
import com.test.entity.User;
import com.test.servicesImpl.UserMongodb;
import com.test.util.ResultUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by 李龙 on 2019-04-29
 **/
@Controller
@RequestMapping("sys")
public class UserMongoController {
    @Autowired
    private UserMongodb userMongodb;

    /**
     * mongoDB insert Data
     * @param admin
     * @return
     */
    @RequestMapping("/insAdmin")
    @RequiresPermissions("sys:admin:save")
    @ResponseBody
    public ResultUtil insAdmin(NewUser admin) {
        //防止浏览器提交
        userMongodb.insert(admin);
        return ResultUtil.ok();
    }

    /**
     * mongoDB Serach DataInfo
     * @return
     */
    @RequestMapping("/NewUserFindAll")
    @ResponseBody
    public List<NewUser> getNewUser(){
        List<NewUser> userList=userMongodb.findAll();
        return userList;
    }
}
