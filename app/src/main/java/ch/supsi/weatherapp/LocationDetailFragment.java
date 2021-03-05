package ch.supsi.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.UUID;

import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;

public class LocationDetailFragment extends Fragment {

    private Location location;

    private TextView locationName;

    public static LocationDetailFragment newInstance(UUID locationID) {
        Bundle args = new Bundle();
        args.putSerializable("locationID", locationID);

        LocationDetailFragment fragment = new LocationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID locationId = (UUID) getArguments().getSerializable("locationID");
        location = UserLocationsHolder.getInstance().findLocation(locationId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_details, container, false);

        locationName = v.findViewById(R.id.location_name);
        locationName.setText(location.getName());

        return v;
    }
}
