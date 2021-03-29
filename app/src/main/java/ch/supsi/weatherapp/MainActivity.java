package ch.supsi.weatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import ch.supsi.weatherapp.controllers.OpenWeather;
import ch.supsi.weatherapp.controllers.SmartLocationController;
import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;
import ch.supsi.weatherapp.model.OnDialogResultListener;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements OnDialogResultListener {
    private static final int COARSE_LOCATION_REQ_CODE = 0x1;

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

        initSmartLocation();

        SmartLocationController.getInstance(this).requestLocation(new Consumer<android.location.Location>() {
            @Override
            public void accept(android.location.Location location) {
                Log.i("FROM CLIENT", "Location : " + location.toString());
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

    private void initSmartLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission not granted");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, COARSE_LOCATION_REQ_CODE);
            }
        } else {
            Log.i(TAG, "Permission granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == COARSE_LOCATION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted");
            }
        }
    }
}