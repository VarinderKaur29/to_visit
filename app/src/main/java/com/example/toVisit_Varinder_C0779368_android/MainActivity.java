package com.example.toVisit_Varinder_C0779368_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.toVisit_Varinder_C0779368_android.databaseHelper.FavouritePlaceDatabase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FavouritePlaceDatabase appDb;
    private RecyclerView recyclerView;
    private TextView tvNoListFound;
    private FloatingActionButton floatingActionButton;
    private FavouriteAdapter favouriteAdapter;
    private ArrayList<FavouriteData> favouriteList = new ArrayList<>();
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initViewsWithData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        tvNoListFound = findViewById(R.id.tv_no_list_found);
        floatingActionButton = findViewById(R.id.floating);
    }

    private void initViewsWithData() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        setAdapter();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getFavouriteList();

    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        favouriteAdapter = new FavouriteAdapter(MainActivity.this, favouriteList, new FavouriteAdapter.OnClickListener() {
            @Override
            public void onClick(FavouriteData favouriteData) {
                showDialogBox(favouriteData);
            }
        });
        recyclerView.setAdapter(favouriteAdapter);
    }

    private void getFavouriteList() {
        @SuppressLint("StaticFieldLeak")
        class GetTasks extends AsyncTask<Void, Void, ArrayList<FavouriteData>> {

            @Override
            protected ArrayList<FavouriteData> doInBackground(Void... voids) {
                appDb = FavouritePlaceDatabase.getInstance(MainActivity.this);
                favouriteList.clear();
                favouriteList.addAll(appDb.favouriteDataDao().getFavouriteList());

                return favouriteList;
            }

            @Override
            protected void onPostExecute(ArrayList<FavouriteData> tasks) {
                super.onPostExecute(tasks);
                favouriteAdapter.notifyDataSetChanged();
                if (favouriteList.size() == 0) {
                    tvNoListFound.setVisibility(View.VISIBLE);
                } else {
                    tvNoListFound.setVisibility(View.GONE);
                }
            }
        }

        GetTasks getTasks = new GetTasks();
        getTasks.execute();
    }

    private void showDialogBox(final FavouriteData favouriteData) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure")
                .setTitle("Delete " + favouriteData.getTitle() + "?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appDb.favouriteDataDao().deleteFavouritePlace(favouriteData);
                        getFavouriteList();
                        favouriteAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
//        alert.setTitle("AlertDialogExample");
        alert.show();
    }
}