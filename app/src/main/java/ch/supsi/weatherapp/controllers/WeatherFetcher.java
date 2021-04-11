package ch.supsi.weatherapp.controllers;

import android.util.Log;

import androidx.core.util.Consumer;

import ch.supsi.weatherapp.OpenWeatherAPI;
import ch.supsi.weatherapp.jsonAPI.Post;
import ch.supsi.weatherapp.model.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class WeatherFetcher {
    public static void fetchWeather(Location location, final Consumer<Post> onResult){
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
                onResult.accept(post);
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("call failture", t.getMessage());

            }
        });
    }

    public static void checkCity(final String locationName, final Consumer<String> onResult, final Consumer<Response<Post>> onFailture){
        Retrofit retrofit = null;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherAPI openWeatherHolderApi = retrofit.create(OpenWeatherAPI.class);
        Call<Post> call = null;
        String city = locationName.replace(" ", "+");
        call = openWeatherHolderApi.getPost( city, "bda36c03a06ee5e92467b7ccb9ecbacc");
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    onFailture.accept(response);
                    Log.e("response unsuccessful", String.valueOf(response.raw()));
                    return;
                }else {
                    Post post = response.body();
                    onResult.accept(locationName);
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("call failture", t.getMessage());
            }
        });
    }
}
