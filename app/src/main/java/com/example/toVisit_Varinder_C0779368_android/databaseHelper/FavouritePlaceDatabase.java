package com.example.toVisit_Varinder_C0779368_android.databaseHelper;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.toVisit_Varinder_C0779368_android.FavouriteData;

//@Database(entities = {FavouriteData.class}, version = 1)
//public abstract class FavouritePlaceDatabase extends RoomDatabase {
//    private static final String DB_NAME = "favourite_place_db";
//    private static FavouritePlaceDatabase instance = null;
//
//    public static synchronized FavouritePlaceDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(context.getApplicationContext(), FavouritePlaceDatabase.class, DB_NAME)
//                    .fallbackToDestructiveMigration()
//                    .build();
//        }
//        return instance;
//    }
//
//    public abstract FavouriteDataDao favouriteDataDao();
//}

@Database(entities = {FavouriteData.class}, version = 1)
public abstract class FavouritePlaceDatabase extends RoomDatabase {
    private static final String DB_NAME = "place_database";

    public abstract FavouriteDataDao favouriteDataDao();

    private static FavouritePlaceDatabase favouritePlaceDb;

    public static FavouritePlaceDatabase getInstance(Context context) {
        if (null == favouritePlaceDb) {
            favouritePlaceDb = buildDatabaseInstance(context);
        }
        return favouritePlaceDb;
    }

    private static FavouritePlaceDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                FavouritePlaceDatabase.class,
                DB_NAME)
                .allowMainThreadQueries().build();
    }

    public void cleanUp() {
        favouritePlaceDb = null;
    }
}

