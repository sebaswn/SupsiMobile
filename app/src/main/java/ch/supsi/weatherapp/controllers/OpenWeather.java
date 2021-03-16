package ch.supsi.weatherapp.controllers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class OpenWeather  extends AsyncTask<String, Void, JSONObject> {
    JSONObject data = null;

    @Override
    protected JSONObject doInBackground(String... strings) {
        try {

            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(strings[0], "UTF-8")+ "&APPID=bda36c03a06ee5e92467b7ccb9ecbacc");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";

            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            data = new JSONObject(json.toString());

            if (data.getInt("cod") != 200) {
                System.out.println("Cancelled");
                return null;
            }


        } catch (Exception e) {
            Log.e("Fail", e.getMessage());
            System.out.println("Exception " + e.getMessage());
            return null;
        }

        if (data != null) {
            System.out.println("HELLO");
            Log.e("my weather received", data.toString());
        }else {
            Log.e("no weather received", data.toString());
        }



        return data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}
