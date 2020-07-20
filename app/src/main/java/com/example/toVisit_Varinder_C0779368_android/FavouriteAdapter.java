package com.example.toVisit_Varinder_C0779368_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FavouriteData> favouriteList;
    private OnClickListener onClickListener;

    public FavouriteAdapter(Context context, ArrayList<FavouriteData> favouriteList, OnClickListener onClickListener) {
        this.context = context;
        this.favouriteList = favouriteList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {
        final FavouriteData data = favouriteList.get(position);
        holder.tvTitle.setText(data.getTitle());
        String distance = String.format(Locale.ENGLISH, "%.2f", data.getDistance());
        String finalDistance = "Distance:" + distance + " KM";
        holder.tvDistance.setText(finalDistance);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("latitude", data.getLatitude());
                intent.putExtra("longitude", data.getLongitude());
                intent.putExtra("title", data.getTitle());
                intent.putExtra("isFromList", true);
                intent.putExtra("placeId", data.getPlace_id());
                context.startActivity(intent);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDistance;
        private ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }

    interface OnClickListener {
        void onClick(FavouriteData favouriteData);
    }
}
