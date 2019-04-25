package com.pe.dellinspi.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.pe.dellinspi.Sql_RoomDatabase.TTB_Users;
import com.pe.dellinspi.Sql_RoomDatabase.UserDao;
import com.pe.dellinspi.Sql_RoomDatabase.UsersDB;

import java.util.List;

public class UserRepository {

    private UsersDB usersDB;
    UserDao userDao;
    UserRepository(Application application){
        usersDB = UsersDB.getInstance(application);
        userDao = usersDB.userDao();
    }

    private boolean insertUsersData(List<TTB_Users> users){
        return userDao.insertUsers(users);
    }

    private LiveData<List<TTB_Users>> getPageWiseUser(int pgNo){
        return userDao.getAllUsers(pgNo);
    }

    private TTB_Users getUserDetails(int uid){
        return userDao.getUserDetails(uid);
    }

}
