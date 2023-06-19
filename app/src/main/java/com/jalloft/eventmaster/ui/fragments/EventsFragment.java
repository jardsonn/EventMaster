package com.jalloft.eventmaster.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.databinding.FragmentEventsBinding;
import com.jalloft.eventmaster.ui.MainActivity;
import com.jalloft.eventmaster.ui.list.EventoListAdapter;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class EventsFragment extends Fragment implements OnNewClickListener {

    private FragmentEventsBinding binding;
    private EventoViewModel viewModel;

    private EventoListAdapter adapter;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private MainActivity mainActivity;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        viewModel.carregarListaEventos();
        adapter = new EventoListAdapter();
        binding.rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEvents.hasFixedSize();
        binding.rvEvents.setAdapter(adapter);
        var activity = ((MainActivity) requireActivity());
        ;

        mainActivity = ((MainActivity) requireActivity());

//        binding.btnAddEvents.setOnClickListener(v -> {
//            navigateAddNewEvent(activity);
//        mainActivity.irParaAdicionarNovoEvento();
//        });

        viewModel.eventosListUiState.observe(getViewLifecycleOwner(), eventoState -> {
            if (eventoState instanceof ResponseUiState.Loading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.includeEmptyLayout.getRoot().setVisibility(View.GONE);
            } else if (eventoState instanceof ResponseUiState.Success) {
                binding.progressCircular.setVisibility(View.GONE);
                var eventos = ((ResponseUiState.Success<List<EventoDTO>>) eventoState).getData();
                binding.includeEmptyLayout.getRoot().setVisibility(!eventos.isEmpty() ? View.GONE : View.VISIBLE);
//                binding.btnAddEvents.setVisibility(eventos.isEmpty() ? View.GONE : View.VISIBLE);
                adapter.setCurrentList(eventos);
            } else if (eventoState instanceof ResponseUiState.Failure) {
                binding.includeEmptyLayout.getRoot().setVisibility(View.VISIBLE);
                binding.progressCircular.setVisibility(View.GONE);
                var erroMsg = ((ResponseUiState.Failure<List<EventoDTO>>) eventoState).getErroMessage();
                Toast.makeText(mainActivity, erroMsg, Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnEventClickListener(eventoDTO -> {
            var currentFragment = activity.getCurrentMainFragment();
            if (currentFragment instanceof MainOptionsFragment) {
                ((MainOptionsFragment) currentFragment).findNavController().navigate(MainOptionsFragmentDirections.actionNavigationMainOptionsToNavigationShowEvent().setCurrentEventId(eventoDTO.getId()));
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAddClick() {
        mainActivity.irParaAdicionarNovoEvento();
    }
}