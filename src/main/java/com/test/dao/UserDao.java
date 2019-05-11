package com.test.dao;

import com.test.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * created by 李龙 on 2019-04-22
 **/
public interface UserDao {
    List<User> selectByUser(User user);
    List<User> userList();
    List<User> selectUserById(@Param(value = "idList") List<Long> idList);
    int deleteUserById(@Param("Id") Long Id);
    int insertUser(User user);
    int updateUser(User user);
}
