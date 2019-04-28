package com.pe.dellinspi.Views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.pe.dellinspi.UserViewModel.UserListViewModel;

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

    TextView pageNoLoaded;
    RecyclerView userList;
    private String pageDefaultText = "Page Loaded : ";
    private int currentPageNo = 1, currentVisibleItems, totalItems, scrollOutItems;
    private boolean isLoadMore = true, isScrolling = false;
    public UserListViewModel userViewModel;
    ProgressBar loadMoreProgress;
    LinearLayoutManager linearLayoutManager;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 4;
    private int currentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INITIALIZE ELEMENTS
        pageNoLoaded = (TextView) findViewById(R.id.pageNoLoaded);
        userList = (RecyclerView) findViewById(R.id.userList);
        loadMoreProgress = (ProgressBar) findViewById(R.id.loadMoreProgress);

        // SET RECYCLER VIEW
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        userList.setLayoutManager(linearLayoutManager);
        final MyAdapter adapter = new MyAdapter();
        userList.setAdapter(adapter);

        userViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);
        if (userViewModel != null) {
            userViewModel.getAllUsers().observe(this, new Observer<List<TTB_Users>>() {
                @Override
                public void onChanged(@Nullable List<TTB_Users> ttb_users) {
                    adapter.setData(ttb_users);
                    if (currentPage == TOTAL_PAGES) {
                        isLastPage = true;
                    }
                    isLoading = false;
                    loadMoreProgress.setVisibility(View.GONE);
                }
            });
        }
        if (isConnectedToInternet()) {
            getUsersPageWise(currentPage);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        userList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getUsersPageWise(currentPage);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


/*userList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isScrolling = true;
                    Log.e("state", "isScrolling" + isScrolling + "...");
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Toast.makeText(getApplicationContext(),"Fetching Further Data",Toast.LENGTH_LONG).show();
                currentVisibleItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                Log.e("state", "currentVisibleItems" + currentVisibleItems);
                Log.e("state", "totalItems" + totalItems);
                Log.e("state", "scrollOutItems" + scrollOutItems);

                Log.e("state", "isScrolling" + isScrolling + "...");

                Log.e("state", "In is Scroll");
                if ((currentVisibleItems + scrollOutItems) == totalItems) {
                    Log.e("state", "Calculation is same");
                    isScrolling = false;
                    currentPageNo = currentPageNo + 1;
                    Log.e("state", "currentPageNo" + currentPageNo);
                    getUsersPageWise(currentPageNo);
                } else {
                    Log.e("state", "Calculation not same");
                }
            }
        });*/
    }

    // FETCH RESPONSE FROM API
    private void getUsersPageWise(final int currentPageNo) {
        loadMoreProgress.setVisibility(View.VISIBLE);
        if (isLoadMore) {
            Toast.makeText(getApplicationContext(), "Loading Page" + currentPageNo + " users", Toast.LENGTH_LONG).show();
            GetUsers RETROFIT_USER = RetrofitBaseClass.getRetrofitClient(this).create(GetUsers.class);

            Call<ResponseObject> call = RETROFIT_USER.getUsers(currentPageNo);
            call.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, retrofit2.Response<ResponseObject> response) {
                    //Toast.makeText(getApplicationContext(), "Loaded " + currentPageNo + " users", Toast.LENGTH_LONG).show();
                    if (response != null) {
                        ResponseObject responseObject = response.body();
                        if (responseObject != null && responseObject.getTotal_pages() != null && responseObject.getTotal_pages().equals("")) {
                            if (Integer.parseInt(responseObject.getPage()) >= Integer.parseInt(responseObject.getTotal_pages())) {
                                Toast.makeText(getApplicationContext(), "Loaded " + "Maximum Available" + " users", Toast.LENGTH_LONG).show();
                                isLoadMore = false;
                                return;
                            }
                        }
                        pageNoLoaded.setText(pageDefaultText + responseObject.getPage() + "/" + responseObject.getTotal_pages());
                        if (responseObject != null && responseObject.getData() != null && responseObject.getData().size() != 0) {
                            saveData(responseObject.getData());
                        }
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.e("ERR_RESPONSE", t.getMessage() + "...");
                    if (t.getMessage().equals("timeout")) {
                        getUsersPageWise(currentPageNo);
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Loaded " + "Maximum Available" + " users", Toast.LENGTH_LONG).show();
        }
    }

    private void saveData(List<UserObject> data) {
        for (int i = 0; i < data.size(); i++) {
            UserObject userObject = data.get(i);
            if (userViewModel != null) {
                userViewModel.insertUser(new TTB_Users(Integer.parseInt(userObject.getId()), userObject.getFirst_name(), userObject.getLast_name(),
                        userObject.getAvatar(), false));
            } else {
                Toast.makeText(getApplicationContext(), "VM Null", Toast.LENGTH_LONG).show();
            }
            Log.e("state", "Saving Data to db");
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
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView uImg;
            TextView uName;

            public MyViewHolder(View itemView) {
                super(itemView);
                uImg = (ImageView) itemView.findViewById(R.id.uImg);
                uName = (TextView) itemView.findViewById(R.id.uName);
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
