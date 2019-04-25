package com.pe.dellinspi.Sql_RoomDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    boolean insertUsers(List<TTB_Users> ttb_users);

    @Query("SELECT * FROM TTB_Users WHERE pageNo = :pageNum ORDER BY userId ASC")
    LiveData<List<TTB_Users>> getAllUsers(int pageNum);

    @Query("SELECT * FROM TTB_Users WHERE userId = :uid LIMIT 1")
    TTB_Users getUserDetails(int uid);

}
