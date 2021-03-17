package ch.supsi.weatherapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import ch.supsi.weatherapp.dao.LocationsDAO;
import ch.supsi.weatherapp.model.Location;

@Database(entities = {Location.class}, version = 1, exportSchema = false)
public abstract class LocationsDB extends RoomDatabase {
    private static final String DATABASE_NAME = "locations_db";
    private static LocationsDB instance;

    public static LocationsDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), LocationsDB.class, LocationsDB.DATABASE_NAME).build();
        return instance;
    }

    public abstract LocationsDAO getLocationsDAO();
}
