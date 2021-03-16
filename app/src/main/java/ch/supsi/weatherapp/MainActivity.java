package ch.supsi.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import ch.supsi.weatherapp.controllers.OpenWeather;
import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private FloatingActionButton newPlaceButton;
    UserLocationsHolder locationsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationsHolder = UserLocationsHolder.getInstance(this);
        locationsHolder.reloadCacheFromDB(new Runnable() {
            @Override
            public void run() {
                reloadPlaces();
            }
        });

        Log.e("Status","getting info");

        new OpenWeather().execute("New York");

        final LocationAdapter adapter = new LocationAdapter(locationsHolder.getAllLocations());

        recyclerView = findViewById(R.id.locations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        newPlaceButton = findViewById(R.id.newPlaceButton);
        newPlaceButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                locationsHolder.insertLocation(
                    new Location("New place #" + locationsHolder.getAllLocations().size()),
                    new Runnable() {
                        @Override
                        public void run() {
                            reloadPlaces();
                        }
                    });
            }
        });

    }

    private void reloadPlaces(){
        ((LocationAdapter)Objects.requireNonNull(recyclerView.getAdapter()))
                .setLocations(locationsHolder.getAllLocations());

        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }
}