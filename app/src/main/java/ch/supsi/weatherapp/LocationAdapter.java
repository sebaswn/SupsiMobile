package ch.supsi.weatherapp;

import android.os.Debug;
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

    private List<Location> locations;

    public LocationAdapter(List<Location> locations) {
        this.locations = locations;
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
        holder.bindTo(locations.get(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
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
                    location.getId()));
        }
    }
}
