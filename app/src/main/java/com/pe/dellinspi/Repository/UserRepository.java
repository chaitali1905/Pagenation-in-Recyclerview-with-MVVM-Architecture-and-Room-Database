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

    public UserRepository(Application application) {
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

    public LiveData<List<TTB_Users>> getAllUsers() {
        return allUsers;
    }

    // GET USER DETAILS || CHECK IF USER EXIST

    public TTB_Users getUser(int uid) throws ExecutionException, InterruptedException {
        return new getUser(userDao).execute(uid).get();
    }

    private static class getUser extends AsyncTask<Integer, Void, TTB_Users> {
        UserDao userDao;

        public getUser(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected TTB_Users doInBackground(Integer... integers) {
            return userDao.getUserDetails(integers[0]);
        }
    }

    // DELETE USER

    public void deleteAllUsers() {
        new deleteUsers(userDao).execute();
    }

    private static class deleteUsers extends AsyncTask<Void, Void, Void> {
        UserDao userDao;
        public deleteUsers(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUsers();
            return null;
        }
    }

}
