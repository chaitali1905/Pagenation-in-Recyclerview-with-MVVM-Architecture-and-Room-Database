package com.pe.dellinspi.Sql_RoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class TTB_Users {

    @PrimaryKey @NonNull
    @ColumnInfo(name = "userId")
    private int id;

    @ColumnInfo(name = "FirstName")
    private String first_name;

    @ColumnInfo(name = "LastName")
    private String last_name;

    @ColumnInfo(name = "userImage")
    private String avatar;

    private int pageNo;

    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPageNo() {
        return pageNo;
    }

    @Ignore
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public TTB_Users(String first_name, String last_name, String avatar, boolean isSelected, int pageNo) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.avatar = avatar;
        this.isSelected = isSelected;
        this.pageNo = pageNo;
    }
}
