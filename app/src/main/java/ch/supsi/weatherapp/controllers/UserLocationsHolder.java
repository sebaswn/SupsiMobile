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
    private final Location currentLocation;
    private List<Location> cachedLocations;
    private final LocationsDB locationsDatabase;

    public UserLocationsHolder(Context context) {
        locationsDatabase = LocationsDB.getInstance(context);
        cachedLocations = new ArrayList<>();
        currentLocation = new Location();
    }

    public static UserLocationsHolder getInstance(Context context) {
        if (instance == null) instance = new UserLocationsHolder(context);
        return instance;
    }

    public void insertLocation(final Location location, final Runnable onLocationAdded) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                locationsDatabase.getLocationsDAO().addLocation(location);
                cachedLocations.add(locationsDatabase.getLocationsDAO().getLocation(location.getName()));
                new Handler(Looper.getMainLooper()).post(onLocationAdded);
            }
        }).start();
    }

    public void reloadCacheFromDB(final Runnable onReloadComplete) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cachedLocations = locationsDatabase.getLocationsDAO().getAllLocations();
                new Handler(Looper.getMainLooper()).post(onReloadComplete);
            }
        }).start();
    }

    public Location getLocation(final int id) {
        for (Location l : cachedLocations) {
            if (l.getId() == id) {
                return l;
            }
        }

        return null;
    }

    public int findIndexOfName(final String name){
        for (int i = 0; i < cachedLocations.size(); i++) {
            Location l = cachedLocations.get(i);
            if (l.getName().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    public int indexOf(final Location location) {
        if (!hasCurrentLocation())
            return cachedLocations.indexOf(location);
        else if (currentLocation.getName().equals(location.getName()))
            return 0;
        else return findIndexOfName(location.getName()) + 1;

    }

    public Location forIndex(int index) {
        if (!hasCurrentLocation())
            return cachedLocations.get(index);
        else if (index == 0)
            return currentLocation;
        else
            return cachedLocations.get(index - 1);
    }

    public List<Location> getAllLocations() {
        return cachedLocations;
    }

    public boolean contains(String locationName) {
        if (locationName.equals(currentLocation.getName())) return true;

        for (Location loc : cachedLocations) {
            if (loc.getName().equals(locationName))
                return true;
        }

        return false;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public int getLocationsCount() {
        if (!hasCurrentLocation()) {
            return cachedLocations.size();
        } else {
            return cachedLocations.size() + 1;
        }
    }

    public boolean hasCurrentLocation() {
        return !currentLocation.getName().isEmpty();
    }
}
