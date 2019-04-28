package com.pe.dellinspi.UserViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.pe.dellinspi.Repository.UserRepository;
import com.pe.dellinspi.Sql_RoomDatabase.TTB_Users;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserListViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    LiveData<List<TTB_Users>> allUsers;

    public UserListViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    public void insertUser(TTB_Users... ttb_users) {
        userRepository.insertUserRepo(ttb_users);
    }

    public LiveData<List<TTB_Users>> getAllUsers() {
        return allUsers;
    }

}
