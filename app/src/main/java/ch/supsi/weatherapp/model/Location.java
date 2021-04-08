package ch.supsi.weatherapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

@Entity(tableName = "Location")
public class Location implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    public Location(String name) {
        this.name = name;
    }

    public Location() {
        name = "";
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
