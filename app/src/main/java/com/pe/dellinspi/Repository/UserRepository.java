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
    }

    // INSERT USERS

    public boolean insertUserRepo(TTB_Users... ttbUsers) throws ExecutionException, InterruptedException {
        new insertAsyncTask(userDao).execute(ttbUsers).get();
        return false;
    }

    private static class insertAsyncTask extends AsyncTask<TTB_Users, Void, Boolean> {
        UserDao userDao;
        insertAsyncTask(UserDao dao) {
            this.userDao = dao;
        }

        @Override
        protected Boolean doInBackground(TTB_Users... ttbUsers) {
            return userDao.insertUsers(ttbUsers[0]);
        }
    }

    // GET ALL USERS

    public LiveData<List<TTB_Users>> getAllUsers(int pgNo) throws ExecutionException, InterruptedException {
        return new getUsersAsyncTask(userDao).execute(pgNo).get();
    }

    private static class getUsersAsyncTask extends AsyncTask<Integer, Void, LiveData<List<TTB_Users>>> {
        UserDao userDao;
        getUsersAsyncTask(UserDao dao) {
            this.userDao = dao;
        }

        @Override
        protected LiveData<List<TTB_Users>> doInBackground(Integer... integers) {
            return userDao.getAllUsers(integers[0]);
        }
    }

}
