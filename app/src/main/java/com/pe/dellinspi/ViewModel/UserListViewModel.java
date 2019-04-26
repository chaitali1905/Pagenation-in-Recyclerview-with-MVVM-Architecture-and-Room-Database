package com.pe.dellinspi.ViewModel;

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

    public UserListViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public boolean insertUser(TTB_Users... ttb_users) throws ExecutionException, InterruptedException {
        return userRepository.insertUserRepo(ttb_users);
    }

    public LiveData<List<TTB_Users>> getAllUsers(int pageNo) throws ExecutionException, InterruptedException {
        return userRepository.getAllUsers(pageNo);
    }

}
