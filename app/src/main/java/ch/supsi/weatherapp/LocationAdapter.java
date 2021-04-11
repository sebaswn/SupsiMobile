package ch.supsi.weatherapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import ch.supsi.weatherapp.model.Location;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationsViewHolder> {

    private Location currentLocation;
    private List<Location> locations;

    public LocationAdapter(Location currentLocation, List<Location> locations) {
        this.currentLocation = currentLocation;
        this.locations = locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @NonNull
    @Override
    public LocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate a new layout_list_location into parent
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_location, parent, false);
        // create the holder for the list from the view
        return new LocationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsViewHolder holder, int position) {
        if(currentLocation.getName().isEmpty())
            holder.bindTo(locations.get(position));
        else if(position == 0)
            holder.bindTo(currentLocation);
        else
            holder.bindTo(locations.get(position-1));
    }

    @Override
    public int getItemCount() {
        return locations.size() + (currentLocation.getName().isEmpty() ? 0 : 1);
    }

    public static class LocationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Location location;
        private TextView text;

        public LocationsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            text = itemView.findViewById(R.id.list_item_text);
        }

        public void bindTo(Location l){
            location = l;
            text.setText(l.getName());
        }

        @Override
        public void onClick(View v) {
            Log.i("INFO", "opening new view with: " + location.getName());
            itemView.getContext().startActivity(LocationDetailsActivity.newIntent(
                    itemView.getContext(),
                    location));
        }
    }
}
