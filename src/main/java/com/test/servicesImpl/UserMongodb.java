package com.test.servicesImpl;

import com.test.entity.NewUser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * created by 李龙 on 2019-04-29
 **/
@Service
public class UserMongodb {
    private static Logger log = Logger.getLogger(UserMongodb.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询所有
     * @return
     */
    public List<NewUser> findAll(){
        List<NewUser> UserList = mongoTemplate.findAll(NewUser.class);
        return UserList;
    }

    /**
     * 更新
     * @param NewUser
     */
    public void update(NewUser NewUser){
        Query query = new Query(Criteria.where("username").is(NewUser.getUsername()));
        Update update = new Update();
        //Update update = Update.update("age",NewUser.getAge());
        update.set("password", NewUser.getUsername());
        update.set("username", NewUser.getPassword());
        update.set("fullname", NewUser.getFullname());

        //更新字段，不插入实体类
        mongoTemplate.updateFirst(query,update,NewUser.class);
        //更新字段同时插入实体类
        // mongoTemplate.updateMulti(query,update,NewUser.class);
    }

    /**
     * 插入
     */
    public void insert(NewUser newUser){
        List<NewUser> list = new ArrayList<>();
        list.add(newUser);
        log.info("插入");
        try {
            mongoTemplate.insert(list,NewUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     */
    public void delete(Object str){
        Query query = new Query(new Criteria("_id").is(str));
        //Object object = mongoTemplate.find(query,NewUser.class);
        try {
            mongoTemplate.findAllAndRemove(query,NewUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}