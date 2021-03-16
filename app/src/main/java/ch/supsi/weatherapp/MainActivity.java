package ch.supsi.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import ch.supsi.weatherapp.controllers.OpenWeather;
import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private FloatingActionButton newPlaceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("Status","getting info");

        new OpenWeather().execute("New York");

        UserLocationsHolder.getInstance().getLocations().add(new Location("Lugano"));
        UserLocationsHolder.getInstance().getLocations().add(new Location("New York"));
        UserLocationsHolder.getInstance().getLocations().add(new Location("bellinzona"));

        LocationAdapter adapter = new LocationAdapter(UserLocationsHolder.getInstance().getLocations());

        recyclerView = findViewById(R.id.locations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        newPlaceButton = findViewById(R.id.newPlaceButton);
        newPlaceButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                UserLocationsHolder.getInstance().getLocations().add(new Location("New Place 1"));
                reloadPlaces();
            }
        });

    }

    private void reloadPlaces(){
        LocationAdapter adapter = new LocationAdapter(UserLocationsHolder.getInstance().getLocations());

        recyclerView = findViewById(R.id.locations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}