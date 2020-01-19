package com.example.potholeproject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Repo;

import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Viewholder> {

    public Context mContext;
    public List<Report> mReportList;

    public MyAdapter(Context context, List<Report> reportList) {
        mContext = context;
        mReportList = reportList;
    }

    @NonNull
    @Override
    public MyAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_report, parent, false);
//        mContext = parent.getContext();
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.Viewholder holder, final int position) {
        Report report = mReportList.get(position);
//        holder.location.setText(report.getLatitude() + " " + report.getLongitude());

        List<Address> addresses = null;

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
             addresses = geocoder.getFromLocation(
                    Double.parseDouble(report.getLatitude()), Double.parseDouble(report.getLongitude()), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);

        holder.location.setText(cityName);
        holder.description.setText(report.getDescription());

        if(report.isServiced()){
            holder.img.setImageResource(R.mipmap.ic_tick);
        } else {
            holder.img.setImageResource(R.mipmap.ic_cross);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Clicked on " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReportList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        TextView location;
        TextView description;
        ImageView img;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.subtitle);
            img = itemView.findViewById(R.id.img);
        }
    }
}
