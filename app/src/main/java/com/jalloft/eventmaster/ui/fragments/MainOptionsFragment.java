package com.jalloft.eventmaster.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.databinding.FragmentMainOptionsBinding;
import com.jalloft.eventmaster.ui.MainActivity;

public class MainOptionsFragment extends Fragment {

    private FragmentMainOptionsBinding binding;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainOptionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var activity = (MainActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);

        var navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.fragment_container_main_options);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            var appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_event, R.id.navigation_lecture, R.id.navigation_room, R.id.navigation_search).build();
            NavigationUI.setupWithNavController(binding.bottomNavView, navController);
            NavigationUI.setupActionBarWithNavController(activity, navController, appBarConfiguration);

            binding.btnFabAddNew.setOnClickListener(v -> {
                var currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (currentFragment instanceof OnNewClickListener) {
                    ((OnNewClickListener) currentFragment).onAddClick();
                }
            });

        }

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (isSearchFragment(navDestination.getId())){
                binding.btnFabAddNew.setVisibility(View.GONE);
//                binding.actionBar.setVisibility(View.GONE);
            }else {
                binding.btnFabAddNew.setVisibility(View.VISIBLE);
//                binding.actionBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isSearchFragment(int id) {
        return R.id.navigation_search == id;
    }

    public NavController findNavController() {
        return NavHostFragment.findNavController(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
