package ch.supsi.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.util.UUID;

import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;

public class LocationDetailsActivity extends AppCompatActivity {

    private ViewPager viewPager;

    public static Intent newIntent(Context packageContext, int locationId) {
        Intent created = new Intent(packageContext, LocationDetailsActivity.class);
        created.putExtra("locationID", locationId);
        return created;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        viewPager = findViewById(R.id.viewpager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return LocationDetailFragment.newInstance(UserLocationsHolder
                        .getInstance(LocationDetailsActivity.this)
                        .getAllLocations()
                        .get(position)
                        .getId());
            }

            @Override
            public int getCount() {
                return UserLocationsHolder.getInstance(LocationDetailsActivity.this).getAllLocations().size();
            }
        });

        int initLocationID = (int) getIntent().getExtras().get("locationID");
        Location initLocation =
                UserLocationsHolder
                .getInstance(LocationDetailsActivity.this)
                .getLocation(initLocationID);

        viewPager.setCurrentItem(UserLocationsHolder
                .getInstance(LocationDetailsActivity.this)
                .indexOf(initLocation));
    }
}
