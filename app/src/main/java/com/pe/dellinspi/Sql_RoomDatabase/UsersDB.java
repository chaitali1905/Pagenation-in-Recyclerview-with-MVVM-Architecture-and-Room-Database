package com.pe.dellinspi.Sql_RoomDatabase;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
@Database(entities = TTB_Users.class, version = 1, exportSchema = false)
public abstract class UsersDB extends RoomDatabase {

    public abstract UserDao userDao(); // EVERY UPPER LAYER HAS A REFERENCE TO ITS LOWER LEVEL IN ARCHITECTURE.
    public static UsersDB instance;
    public static UsersDB getInstance(Application application) {
        if (instance == null){
            instance = Room.databaseBuilder(application, UsersDB.class, "udb").build();
        }
        return instance;
    }
}
