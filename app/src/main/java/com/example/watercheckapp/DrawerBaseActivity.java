package com.example.watercheckapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Fragment;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.watercheckapp.alarms.AlarmActivity;
import com.example.watercheckapp.history.HistoryActivity;
import com.example.watercheckapp.sensors.SensorsActivity;
import com.example.watercheckapp.home.HomeActivity;
import com.example.watercheckapp.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_start,R.string.menu_drawer_stop);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Fragment fragment = null;
        Class fragmentClass;
        switch(item.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0,0);
                //replaceFragment(new HomeFragment());
                break;
            case R.id.nav_map:

                startActivity(new Intent(this,MapActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_sensors:
                startActivity(new Intent(this, SensorsActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_alarms:
                startActivity(new Intent(this, AlarmActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_history:
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0,0);
                break;
        }
        return false;

    }
    protected void allocateActivityTitle(String title){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(title);
        }

    }
    private void replaceFragment(androidx.fragment.app.Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityContainer,fragment);
        fragmentTransaction.commit();



    }
}