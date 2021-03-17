package ch.supsi.weatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import ch.supsi.weatherapp.controllers.OpenWeather;
import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;
import ch.supsi.weatherapp.model.OnDialogResultListener;


public class MainActivity extends AppCompatActivity implements OnDialogResultListener {

    RecyclerView recyclerView;
    private FloatingActionButton newPlaceButton;
    UserLocationsHolder locationsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationsHolder = UserLocationsHolder.getInstance(this);

        Log.e("Status", "getting info");

        new OpenWeather().execute("New York");
        LocationAdapter adapter = new LocationAdapter(locationsHolder.getAllLocations());

        recyclerView = findViewById(R.id.locations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        locationsHolder.reloadCacheFromDB(new Runnable() {
            @Override
            public void run() {
                reloadPlaces();
            }
        });

        newPlaceButton = findViewById(R.id.newPlaceButton);
        newPlaceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialogAndGetResult("Insert new city:", "", "San Francisco", MainActivity.this);
            }
        });

    }

    public void showDialogAndGetResult(final String title, final String message, final String initialText, final OnDialogResultListener listener) {
        final EditText editText = new EditText(this);
        editText.setText(initialText);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onDialogResult(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setView(editText)
                .show();
    }

    @Override
    public void onDialogResult(String result) {
        UserLocationsHolder.getInstance(this).insertLocation(new Location(result), new Runnable() {
            @Override
            public void run() {
                reloadPlaces();
            }
        });
    }

    private void reloadPlaces() {
        ((LocationAdapter) Objects.requireNonNull(recyclerView.getAdapter()))
                .setLocations(locationsHolder.getAllLocations());

        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }
}