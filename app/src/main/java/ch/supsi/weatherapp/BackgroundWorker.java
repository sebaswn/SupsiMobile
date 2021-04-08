package ch.supsi.weatherapp;

import android.app.NotificationManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ch.supsi.weatherapp.controllers.SmartLocationController;
import ch.supsi.weatherapp.controllers.WeatherFetcher;
import ch.supsi.weatherapp.jsonAPI.Post;

public class BackgroundWorker extends Worker {
    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private double kelvinToCelsius(double k) {
        return k - 273.15;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("INFO", "started background check");
        Geocoder geocoder = new Geocoder(
                getApplicationContext(), Locale.getDefault());
        Location location = SmartLocationController.getInstance(getApplicationContext()).getLastLocation();
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            final ch.supsi.weatherapp.model.Location toFetch = new ch.supsi.weatherapp.model.Location(addresses.get(0).getLocality());

            Log.i("INFO", "location: " + toFetch.getName());
            WeatherFetcher.fetchWeather(toFetch, new Consumer<Post>() {
                @Override
                public void accept(Post post) {
                    double currentTemperature = kelvinToCelsius(post.getMain().getTemp());
                    Log.i("INFO", "temperature: " + currentTemperature);
                    if (currentTemperature < 0 || currentTemperature > 25) {
                        Log.i("INFO", "sending notification");
                        sendTemperatureNotification(toFetch, currentTemperature);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success();
    }

    private void sendTemperatureNotification(ch.supsi.weatherapp.model.Location toFetch, double temperature) {
        NotificationManager mNotificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
// creo il contenuto della notifica
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Registered critical temperature in you zone")
                .setContentText("Temperature at " + toFetch.getName() + ": " + temperature + "°C")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(MainActivity.CRIT_TEMP_CH_ID);

        //mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
