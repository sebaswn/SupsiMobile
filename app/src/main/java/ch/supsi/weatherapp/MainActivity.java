package ch.supsi.weatherapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ch.supsi.weatherapp.controllers.SmartLocationController;
import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.controllers.WeatherFetcher;
import ch.supsi.weatherapp.model.Location;
import ch.supsi.weatherapp.model.OnDialogResultListener;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements OnDialogResultListener {
    public static final int LOCATION_REQ_CODE = 0x1;
    public static final String CRIT_TEMP_CH_ID = "CritTempChannel";

    RecyclerView recyclerView;
    WeatherFetcher weatherFetcher;

    private FloatingActionButton newPlaceButton;
    UserLocationsHolder locationsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationsHolder = UserLocationsHolder.getInstance(this);

        Log.e("Status", "getting info");

        LocationAdapter adapter = new LocationAdapter(locationsHolder.getCurrentLocation(), locationsHolder.getAllLocations());

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
                showDialogAndGetResult("Insert new city", "Location name:", "", MainActivity.this);
            }
        });

        initNotificationChannel();
        initSmartLocation();
        startCriticalTempMonitor();
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
        //String cityName;

        weatherFetcher.checkCity(result, (cityName) -> {
            UserLocationsHolder.getInstance(this).insertLocation(new Location(cityName), new Runnable() {
                @Override
                public void run() {
                    reloadPlaces();
                }
            });
        }, (response) -> {
            Toast.makeText(getApplicationContext(), result + " could not be found", 15).show();
            Log.e("response unsuccessful", String.valueOf(response.raw()));
        });


    }

    private void reloadPlaces() {
        ((LocationAdapter) Objects.requireNonNull(recyclerView.getAdapter()))
                .setLocations(locationsHolder.getAllLocations());
        ((LocationAdapter) Objects.requireNonNull(recyclerView.getAdapter()))
                .setCurrentLocation(locationsHolder.getCurrentLocation());

        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }

    private void initSmartLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission not granted");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
            }
        } else {
            Log.i(TAG, "Permission granted");
            //updateCurrentLocation();
            monitorCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted");
                monitorCurrentLocation();
            }
        }
    }

    private void updateCurrentLocation() {
        SmartLocationController.getInstance(this).requestLocation(new Consumer<android.location.Location>() {
            @Override
            public void accept(final android.location.Location location) {
                // send a call to get weather for coordinates in location
                // then take name and create a local location with that name if not already existing

                Geocoder geocoder = new Geocoder(
                        MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Location current = new Location(addresses.get(0).getLocality());

                    if (!locationsHolder.contains(current.getName()))
                        locationsHolder.insertLocation(current, new Runnable() {
                            @Override
                            public void run() {
                                reloadPlaces();
                            }
                        });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void monitorCurrentLocation() {
        SmartLocationController.getInstance(this).startMonitoring(new Consumer<android.location.Location>() {
            @Override
            public void accept(final android.location.Location location) {

                Geocoder geocoder = new Geocoder(
                        MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    Location current = locationsHolder.getCurrentLocation();
                    current.setName(addresses.get(0).getLocality());
                    reloadPlaces();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startCriticalTempMonitor() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class,
                15, TimeUnit.MINUTES).setConstraints(constraints).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("POLL WORK",
                ExistingPeriodicWorkPolicy.KEEP, periodicRequest);
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CRIT_TEMP_CH_ID, "Critical Temperatures", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Send critical temperatures of your current zone as notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}