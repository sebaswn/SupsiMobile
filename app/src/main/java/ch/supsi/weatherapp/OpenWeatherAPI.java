package ch.supsi.weatherapp;

import ch.supsi.weatherapp.jsonAPI.Post;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherAPI {

    @GET("weather")
    Call<Post> getPost(@Query(value="q", encoded = true) String city, @Query("APPID") String appid);
}
