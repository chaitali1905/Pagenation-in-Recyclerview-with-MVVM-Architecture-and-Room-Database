package com.pe.dellinspi.Views;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.pe.dellinspi.R;
import com.pe.dellinspi.RetrofitUtilityFiles.ResponseObject;
import com.pe.dellinspi.RetrofitUtilityFiles.RetrofitBaseClass;
import com.pe.dellinspi.RetrofitUtilityFiles.UserObject;
import com.pe.dellinspi.Sql_RoomDatabase.TTB_Users;
import com.pe.dellinspi.UserViewModel.UserVM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    // INTERFACE FOR RETROFIT
    public interface GetUsers {
        @GET("/api/users")
        Call<ResponseObject> getUsers(@Query("page") int pageNo);
    }

    TextView pageNoLoaded, noDataTv;
    RecyclerView userList;
    private String pageDefaultText = "Page Loaded : ";
    private int currentPageNo = 1, currentVisibleItems, totalItems, scrollOutItems;
    private boolean isLoadMore = true, isScrolling = true;
    public UserVM userViewModel;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;
    private int currentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ATTACH VIEW MODEL
        userViewModel = ViewModelProviders.of(this).get(UserVM.class);

        /*
        CLEARING ROOM DATABASE TO IMPLEMENT PAGENATION EVERY TIME APP IS REOPENED.
        OTHER WISE THE DATA FROM TABLE IS DIRECTLY LOADED AND PAGENATION WILL NOT BE
        CHECKED
        */
        userViewModel.deleteAll();

        // INITIALIZE ELEMENTS
        pageNoLoaded = (TextView) findViewById(R.id.pageNoLoaded);
        userList = (RecyclerView) findViewById(R.id.userList);
        loadMoreProgress = (ProgressBar) findViewById(R.id.loadMoreProgress);
        noDataTv = (TextView) findViewById(R.id.noData);

        // SET RECYCLER VIEW
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        userList.setLayoutManager(linearLayoutManager);
        final MyAdapter adapter = new MyAdapter();
        userList.setAdapter(adapter);
        userList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoadMore && isScrolling) {
                    currentVisibleItems = linearLayoutManager.getChildCount();
                    totalItems = linearLayoutManager.getItemCount();
                    scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if ((currentVisibleItems + scrollOutItems) == totalItems) {
                        isScrolling = false;
                        currentPageNo = currentPageNo + 1;
                        getUsersPageWise(currentPageNo);
                    }
                }
            }
        });

        // BIND VIEW WITH DATA
        if (userViewModel != null) {
            userViewModel.getAllUsers().observe(this, new Observer<List<TTB_Users>>() {
                @Override
                public void onChanged(@Nullable List<TTB_Users> ttb_users) {
                    if (ttb_users != null && ttb_users.size() != 0) {
                        adapter.setData(ttb_users);
                        userList.setVisibility(View.VISIBLE);
                        noDataTv.setVisibility(View.GONE);
                    } else {
                        userList.setVisibility(View.GONE);
                        noDataTv.setVisibility(View.VISIBLE);
                    }
                    loadMoreProgress.setVisibility(View.GONE);
                }
            });
        }

        // GET DATA
        if (isConnectedToInternet()) {
            noDataTv.setText("Loading users...");
            getUsersPageWise(currentPage);
        }
    }

    // FETCH RESPONSE FROM API
    private void getUsersPageWise(final int currentPageNo) {
        loadMoreProgress.setVisibility(View.VISIBLE);
        if (isLoadMore) {
            GetUsers RETROFIT_USER = RetrofitBaseClass.getRetrofitClient(this).create(GetUsers.class);
            Call<ResponseObject> call = RETROFIT_USER.getUsers(currentPageNo);
            call.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, retrofit2.Response<ResponseObject> response) {
                    if (response != null) {
                        ResponseObject responseObject = response.body();
                        if (responseObject != null && responseObject.getData() != null && responseObject.getData().size() != 0) {
                            try {
                                saveData(responseObject.getData());
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (responseObject != null && responseObject.getTotal_pages() != null && !responseObject.getTotal_pages().equals("") && responseObject.getPage() != null && !responseObject.getPage().equals("")) {
                            if (Integer.parseInt(responseObject.getPage()) >= Integer.parseInt(responseObject.getTotal_pages())) {
                                isLoadMore = false;
                                loadMoreProgress.setVisibility(View.GONE);
                                return;
                            }
                        }
                        loadMoreProgress.setVisibility(View.GONE);
                        pageNoLoaded.setText(pageDefaultText + responseObject.getPage() + "/" + responseObject.getTotal_pages());
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.e("ERR_RESPONSE", t.getMessage() + "...");
                    if (t.getMessage().equals("timeout")) {
                        showTryAgainAlert(currentPageNo); // TRY AGAIN POP UP
                    }
                }
            });
        }
    }

    // TRY AGAIN POP UP
    public void showTryAgainAlert(final int currentPageNo) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Taking too much time to fetch users. Please Try Again.");
        alertDialog.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getUsersPageWise(currentPageNo);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(linearLayoutManager.getItemCount() != 0){
                    loadMoreProgress.setVisibility(View.GONE);
                } else {
                    userList.setVisibility(View.GONE);
                    noDataTv.setText("Taking too much time to fetch users. Please Try Again later.");
                    noDataTv.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    // SAVE DATA TO ROOM DATABASE
    private void saveData(List<UserObject> data) throws ExecutionException, InterruptedException {
        for (int i = 0; i < data.size(); i++) {
            UserObject userObject = data.get(i);
            if (userViewModel != null) {
                if (userViewModel.getUser(Integer.parseInt(userObject.getId())) == null) {
                    userViewModel.insertUser(new TTB_Users(Integer.parseInt(userObject.getId()), userObject.getFirst_name(), userObject.getLast_name(),
                            userObject.getAvatar(), false));
                }
            }
        }
    }

    // RECYCLERVIEW ADAPTER
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<TTB_Users> list = new ArrayList<>();

        public MyAdapter() {
        }

        public void setData(List<TTB_Users> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, null));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final TTB_Users user = list.get(position);

            RequestBuilder<Drawable> thumbnailRequest = Glide
                    .with(MainActivity.this)
                    .load(R.mipmap.placeholder);
            Glide.with(getApplicationContext())
                    .load(user.getAvatar())
                    .thumbnail(thumbnailRequest)
                    .into(holder.uImg);

            holder.uName.setText(user.getFirst_name() + " " + user.getLast_name());
            holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, UserDetailsActivity.class).putExtra("uid", user.getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView uImg;
            TextView uName;
            CheckBox check;
            ConstraintLayout getDetailsLayout;

            public MyViewHolder(View itemView) {
                super(itemView);
                uImg = (ImageView) itemView.findViewById(R.id.uImg);
                uName = (TextView) itemView.findViewById(R.id.uName);
                getDetailsLayout = (ConstraintLayout) itemView.findViewById(R.id.getDetailsLayout);
            }
        }
    }

    // CHECK IF DEVICE IS CONNECTED TO INTERNET
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
