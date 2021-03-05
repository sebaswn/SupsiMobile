package ch.supsi.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserLocationsHolder.getInstance().getLocations().add(new Location("Strunz 14"));
        UserLocationsHolder.getInstance().getLocations().add(new Location("Strunz 2"));
        UserLocationsHolder.getInstance().getLocations().add(new Location("Strunz 3"));

        LocationAdapter adapter = new LocationAdapter(UserLocationsHolder.getInstance().getLocations());

        recyclerView = findViewById(R.id.locations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
}