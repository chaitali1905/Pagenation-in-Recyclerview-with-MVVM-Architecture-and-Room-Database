package com.pe.dellinspi.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.pe.dellinspi.Sql_RoomDatabase.TTB_Users;
import com.pe.dellinspi.Sql_RoomDatabase.UserDao;
import com.pe.dellinspi.Sql_RoomDatabase.UsersDB;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserRepository {
    private UserDao userDao;
    LiveData<List<TTB_Users>> allUsers;
    public UserRepository(Application application){
        UsersDB usersDB = UsersDB.getInstance(application);
        userDao = usersDB.userDao();
        allUsers = userDao.getAllUsers();
    }

    // INSERT USERS

    public void insertUserRepo(TTB_Users... ttbUsers) {
        new insertAsyncTask(userDao).execute(ttbUsers);
    }

    private static class insertAsyncTask extends AsyncTask<TTB_Users, Void, Void> {
        UserDao userDao;
        insertAsyncTask(UserDao dao) {
            this.userDao = dao;
        }

        @Override
        protected Void doInBackground(TTB_Users... ttbUsers) {
           userDao.insertUsers(ttbUsers[0]);
            return null;
        }
    }

    // GET ALL USERS

    public LiveData<List<TTB_Users>> getAllUsers(){
        return allUsers;
    }

}
