package com.pe.dellinspi.Views;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.pe.dellinspi.R;
import com.pe.dellinspi.Sql_RoomDatabase.TTB_Users;
import com.pe.dellinspi.UserViewModel.UserVM;

import java.util.concurrent.ExecutionException;

public class UserDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        UserVM userViewModel = ViewModelProviders.of(this).get(UserVM.class);
        if(getIntent() != null && getIntent().hasExtra("uid") && userViewModel != null){
            try {
                TTB_Users user = userViewModel.getUser(getIntent().getIntExtra("uid",0));
                if(user != null){
                    ImageView userImage = (ImageView) findViewById(R.id.userImage);
                    RequestBuilder<Drawable> thumbnailRequest = Glide.with(this).load(R.mipmap.placeholder);
                    Glide.with(getApplicationContext())
                            .load(user.getAvatar())
                            .thumbnail(thumbnailRequest)
                            .into(userImage);
                    TextView userName = (TextView) findViewById(R.id.userName);
                    userName.setText(user.getFirst_name() + " " + user.getLast_name());
                } else {
                    showAlert();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            showAlert();
        }
    }

    public void showAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("User Details unavailable. Please Try Again later");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               finish();
            }
        });
        alertDialog.show();
    }

}
