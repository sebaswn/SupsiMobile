package ch.supsi.weatherapp.model;

import java.util.UUID;

public class Location {
    private UUID id;
    private String name;

    public Location(String name) {
        this.name = name;
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
