package ch.supsi.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.util.List;
import java.util.UUID;

import ch.supsi.weatherapp.controllers.UserLocationsHolder;
import ch.supsi.weatherapp.model.Location;

public class LocationDetailsActivity extends AppCompatActivity {

    private ViewPager viewPager;

    public static Intent newIntent(Context packageContext, UUID locationId) {
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
                return LocationDetailFragment.newInstance(UserLocationsHolder.getInstance().getLocations().get(position).getId());
            }

            @Override
            public int getCount() {
                return UserLocationsHolder.getInstance().getLocations().size();
            }
        });

        UUID initLocationID = (UUID)getIntent().getExtras().get("locationID");
        viewPager.setCurrentItem(UserLocationsHolder.getInstance().indexOf(initLocationID));
    }
}
