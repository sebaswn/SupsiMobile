package ch.supsi.weatherapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.URL;

import ch.supsi.weatherapp.controllers.WeatherFetcher;
import ch.supsi.weatherapp.jsonAPI.Post;
import ch.supsi.weatherapp.model.Location;


public class LocationDetailFragment extends Fragment {

    private Location location;

    private TextView locationName;
    private TextView degreesNow;
    private TextView degreesMax;
    private TextView degreesMin;
    private ImageView imageView;

    public static LocationDetailFragment newInstance(Location location) {
        Bundle args = new Bundle();
        args.putSerializable("location", location);

        LocationDetailFragment fragment = new LocationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = (Location) getArguments().getSerializable("location");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_details, container, false);

        locationName = v.findViewById(R.id.location_name);
        degreesNow = v.findViewById(R.id.degrees_now);
        degreesMin = v.findViewById(R.id.degrees_max);
        degreesMax = v.findViewById(R.id.degrees_min);
        imageView = v.findViewById(R.id.weather_status_img);

        locationName.setText(location.getName());
        getAPIInfo();


        return v;
    }

    private double kelvinToCelsius(double k) {
        return k - 273.15;
    }

    public void getAPIInfo() {
        WeatherFetcher.fetchWeather(location, new Consumer<Post>() {
            @Override
            public void accept(final Post post) {
                degreesNow.setText(String.format("%.1f°C", kelvinToCelsius(post.getMain().getTemp())));
                degreesMin.setText(String.format("%.1f°C", kelvinToCelsius(post.getMain().getTempMin())));
                degreesMax.setText(String.format("%.1f°C", kelvinToCelsius(post.getMain().getTempMax())));

                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL thumb_u = new URL("https://openweathermap.org/img/wn/" +
                                        post.getWeather().get(0).getIcon() +
                                        "@2x.png");

                                final Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");


                                new Handler(getContext().getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageDrawable(thumb_d);

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
