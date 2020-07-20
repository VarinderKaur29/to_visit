package com.example.toVisit_Varinder_C0779368_android;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class FavouriteData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int place_id;
    @ColumnInfo(name = "title")
    private String title = null;
    @ColumnInfo(name = "latitude")
    private double latitude = 0;
    @ColumnInfo(name = "longitude")
    private double longitude = 0;
    @ColumnInfo(name = "distance")
    private float distance = 0f;

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
