package com.example.toVisit_Varinder_C0779368_android.databaseHelper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.toVisit_Varinder_C0779368_android.FavouriteData;

import java.util.List;

@Dao
public interface FavouriteDataDao {
    @Query("Select * from favouritedata")
    List<FavouriteData> getFavouriteList();

    @Insert
    void insertFavouritePlaceData(FavouriteData favouriteData);

    @Update
    void updateFavouriteData(FavouriteData favouriteData);

    @Delete
    void deleteFavouritePlace(FavouriteData favouriteData);
}
