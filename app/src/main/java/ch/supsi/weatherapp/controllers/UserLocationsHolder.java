package ch.supsi.weatherapp.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import ch.supsi.weatherapp.database.LocationsDB;
import ch.supsi.weatherapp.model.Location;

public class UserLocationsHolder {
    private static UserLocationsHolder instance;
    private List<Location> cachedLocations;
    private final LocationsDB locationsDatabase;

    public UserLocationsHolder(Context context) {
        locationsDatabase = LocationsDB.getInstance(context);
        cachedLocations = new ArrayList<>();
    }

    public static UserLocationsHolder getInstance(Context context) {
        if(instance == null) instance = new UserLocationsHolder(context);
        return instance;
    }

    public void insertLocation(final Location location, final Runnable onLocationAdded){
        new Thread(new Runnable() {
            @Override
            public void run() {
                locationsDatabase.getLocationsDAO().addLocation(location);
                cachedLocations.add(locationsDatabase.getLocationsDAO().getLocation(location.getName()));
                new Handler(Looper.getMainLooper()).post(onLocationAdded);
            }
        }).start();
    }

    public void reloadCacheFromDB(final Runnable onReloadComplete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                cachedLocations = locationsDatabase.getLocationsDAO().getAllLocations();
                new Handler(Looper.getMainLooper()).post(onReloadComplete);
            }
        }).start();
    }

    public Location getLocation(final int id){
        for(Location l: cachedLocations){
            if(l.getId() == id){
                return l;
            }
        }

        return null;
    }

    public int indexOf(final Location location){
        return cachedLocations.indexOf(location);
    }

    public List<Location> getAllLocations(){
        return cachedLocations;
    }

    public boolean contains(String locationName){
        for(Location loc : cachedLocations){
            if(loc.getName().equals(locationName))
                return true;
        }

        return false;
    }
}
