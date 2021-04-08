package ch.supsi.weatherapp.controllers;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import androidx.core.util.Consumer;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class SmartLocationController {
    private static SmartLocationController instance;
    private boolean initialized;
    private final Context context;

    public static final LocationParams params = new
            LocationParams.Builder().setAccuracy(LocationAccuracy.HIGH).setDistance(100).setInterval(100).build();

    private SmartLocationController(Context c){
        context = c;
    };

    public static SmartLocationController getInstance(Context c) {
        if(instance==null) instance = new SmartLocationController(c);
        return instance;
    }

    public void startMonitoring(final Consumer<Location> onResult){
        SmartLocation.with(context).location().continuous().config(params)
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        onResult.accept(location);
                    }
                });
    }

    public void requestLocation(final Consumer<Location> onLocationResult){
        SmartLocation.with(context).location().oneFix().config(params).start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                Log.i("Location", location.toString());
                onLocationResult.accept(location);
            }
        });
    }

    public Location getLastLocation(){
        return SmartLocation.with(context).location().getLastLocation();
    }
}
