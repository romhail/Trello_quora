package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SignupBusinessService {

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity signup(UsersEntity usersEntity) throws SignUpRestrictedException {

        if(userDao.getUserByUserName(usersEntity.getUsername())!=null){ throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken"); }


        if(userDao.getUserByEmail(usersEntity.getEmail())!=null){ throw new SignUpRestrictedException(" SGR-002"," This user has already been registered, try with any other emailId"); }

        return this.userDao.createUsers(usersEntity);


    }


}
