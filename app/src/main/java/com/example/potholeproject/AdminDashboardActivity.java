package com.example.potholeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    BottomNavigationView navbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "AdminDashboardActivity";
    private int count;
    List<Report> mReportList;
    ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        //navbar = findViewById(R.id.bottom_nav_bar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mReportList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Reports");

        count = 0;
//
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                reportList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Report report = snapshot.getValue(Report.class);

                    addReport(report);
                }
                showReports();
                mAdapter = new MyAdapter(getApplicationContext(),mReportList);
                mRecyclerView.setAdapter(new MyAdapter(getApplicationContext(), mReportList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setItemPrefetchEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map:
                startActivity(new Intent(AdminDashboardActivity.this,AdminMapActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showReports() {
        for(Report report : mReportList){
            Log.d(TAG, "onCreate: " + report.getDescription());
        }
    }

    private void addReport(Report report) {
        mReportList.add(report);
    }

    private void increaseCount() {
        count++;
    }
}
