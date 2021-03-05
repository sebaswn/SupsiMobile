package ch.supsi.weatherapp.controllers;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.supsi.weatherapp.model.Location;

public class UserLocationsHolder {
    private static UserLocationsHolder instance;
    private List<Location> locations;

    public UserLocationsHolder() {
        locations = new ArrayList<>();
    }

    public static UserLocationsHolder getInstance() {
        if(instance == null) instance = new UserLocationsHolder();
        return instance;
    }

    public Location findLocation(UUID id){
        for (Location l :
                locations) {
            if (l.getId().equals(id)){
                return l;
            };
        }

        return null;
    }

    public int indexOf(UUID locationID){
        for(int i=0;i<locations.size(); i++){
            if(locationID.equals(locations.get(i).getId())) return i;
        }

        return -1;
    }

    public List<Location> getLocations() {
        return locations;
    }
}
