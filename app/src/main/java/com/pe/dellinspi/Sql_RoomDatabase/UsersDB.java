package com.pe.dellinspi.Sql_RoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {TTB_Users.class}, version = 1, exportSchema = false)
public abstract class UsersDB extends RoomDatabase {

    public abstract UserDao userDao(); // EVERY UPPER LAYER HAS A REFERENCE TO ITS LOWER LEVEL IN ARCHITECTURE.
    private static UsersDB instance;
    public static synchronized UsersDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, UsersDB.class, "UserDatabase").build();
        return instance;
    }

}
