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
import ch.supsi.weatherapp.controllers.OpenWeather;
import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;
import ch.supsi.weatherapp.model.OnDialogResultListener;


public class MainActivity extends AppCompatActivity implements OnDialogResultListener {

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
        UserLocationsHolder.getInstance().getLocations().add(new Location("Bellinzona"));

        loadPlacesList();

        newPlaceButton = findViewById(R.id.newPlaceButton);
        newPlaceButton.setOnClickListener(new View.OnClickListener(){

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
        UserLocationsHolder.getInstance().getLocations().add(new Location(result));
        loadPlacesList();
    }

    private void loadPlacesList(){
        LocationAdapter adapter = new LocationAdapter(UserLocationsHolder.getInstance().getLocations());

        recyclerView = findViewById(R.id.locations_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}