package ch.supsi.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.jsonAPI.Post;
import ch.supsi.weatherapp.model.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationDetailFragment extends Fragment {

    private Location location;

    private TextView locationName;
    private TextView degreesNow;
    private TextView degreesMax;
    private TextView degreesMin;

    public static LocationDetailFragment newInstance(int locationID) {
        Bundle args = new Bundle();
        args.putSerializable("locationID", locationID);

        LocationDetailFragment fragment = new LocationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int locationId = (int) getArguments().getSerializable("locationID");
        location = UserLocationsHolder.getInstance(this.getContext()).getLocation(locationId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_details, container, false);

        locationName = v.findViewById(R.id.location_name);
        degreesNow = v.findViewById(R.id.degrees_now);
        degreesMin = v.findViewById(R.id.degrees_max);
        degreesMax = v.findViewById(R.id.degrees_min);

        locationName.setText(location.getName());
        getAPIInfo();

        return v;
    }

    public double convertTemperature(double k){
        k = k - 273.15;
        k = Math.round(k*100)/100;
        return k ;
    }

    public void getAPIInfo(){
        Retrofit retrofit = null;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherAPI openWeatherHolderApi = retrofit.create(OpenWeatherAPI.class);
        Call<Post> call = null;
        String city = location.getName().replace(" ", "+");
        call = openWeatherHolderApi.getPost( city, "bda36c03a06ee5e92467b7ccb9ecbacc");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    Log.e("response unsuccessful", String.valueOf(response.raw()));
                    return;
                }
                Post post = response.body();
                degreesNow.setText( convertTemperature(post.getMain().getTemp()) + "°C");
                degreesMin.setText( convertTemperature(post.getMain().getTempMin()) + "°C");
                degreesMax.setText( convertTemperature(post.getMain().getTempMax()) + "°C");


            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("call failture", t.getMessage());

            }
        });

    }
}
