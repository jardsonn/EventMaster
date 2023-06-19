package com.jalloft.eventmaster.ui;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.AppBarConfigurationKt;
import androidx.navigation.ui.NavigationUI;

import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.databinding.ActivityMainBinding;
import com.jalloft.eventmaster.ui.fragments.MainOptionsFragment;
import com.jalloft.eventmaster.ui.fragments.MainOptionsFragmentDirections;

import android.view.Menu;
import android.view.MenuItem;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSupportActionBar(binding.toolbar);
//
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

//        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_event, R.id.navigation_lecture, R.id.navigation_room, R.id.navigation_search).build();
//        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    public NavHostFragment getNavHostFragment(){
        return navHostFragment;
    }

    public Fragment getCurrentMainFragment(){
        return navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    public void irParaAdicionarNovoEvento() {
        var currentFragment = getCurrentMainFragment();
        if (currentFragment instanceof MainOptionsFragment) {
            ((MainOptionsFragment) currentFragment).findNavController().navigate(MainOptionsFragmentDirections.actionNavigationMainOptionsToNavigationNewEvent());
        }
    }



//    public FloatingActionButton getFab() {
//        if (binding == null) return null;
//        return binding.btnAddEvents;
//    }

//    public void addFragmentOption(Fragment fragment) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container_options, fragment)
//                .commit();
//    }
//
//    public void removeFragment() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment fragment = (Fragment) fragmentManager.findFragmentById(R.id.fragment_container_options);
//        if (fragment != null) {
//            fragmentTransaction.remove(fragment);
//            fragmentTransaction.commit();
//        }
//    }


}