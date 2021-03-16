package ch.supsi.weatherapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import ch.supsi.weatherapp.model.Location;

@Dao
public interface LocationsDAO {

    @Query("SELECT * FROM Location")
    List<Location> getAllLocations();

    @Insert
    void addLocation(Location location);

    @Delete
    void removeLocation(Location location);

    @Query("SELECT * FROM Location WHERE id = :locID")
    Location getLocation(int locID);

    @Query("SELECT * FROM Location WHERE name = :locName")
    Location getLocation(String locName);

    @Query("SELECT COUNT(id) FROM Location")
    int getLocationsCount();

    @Update
    void updateLocation(Location l);
}
