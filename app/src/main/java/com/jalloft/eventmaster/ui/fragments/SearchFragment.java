package com.jalloft.eventmaster.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.ChipGroup;
import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.PalestraDTO;
import com.jalloft.eventmaster.databinding.FragmentSearchBinding;
import com.jalloft.eventmaster.ui.list.PalestraListAdapter;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;
import com.jalloft.eventmaster.ui.viewmodel.SearchBy;
import com.jalloft.eventmaster.utils.MyTextWatcher;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private EventoViewModel viewModel;

    private PalestraListAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PalestraListAdapter();
        binding.rvLecture.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvLecture.hasFixedSize();
        binding.rvLecture.setAdapter(adapter);

        binding.editQuery.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchBy searchBy = binding.chipPorTitulo.isChecked() ? SearchBy.LECTURE_TITLE : SearchBy.SPEAKER_NAME;
                viewModel.buscarPalestras(s.toString(), searchBy);
            }
        });

        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            SearchBy searchBy = binding.chipPorTitulo.isChecked() ? SearchBy.LECTURE_TITLE : SearchBy.SPEAKER_NAME;
            viewModel.buscarPalestras(binding.editQuery.getText().toString(), searchBy);
        });

        viewModel.buscarPalestrasListUiState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ResponseUiState.Loading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.textNotResult.setVisibility(View.GONE);
            } else if (state instanceof ResponseUiState.Success) {
                binding.progressCircular.setVisibility(View.GONE);
                var eventos = ((ResponseUiState.Success<List<PalestraDTO>>) state).getData();
                binding.textNotResult.setVisibility(!eventos.isEmpty() ? View.GONE : View.VISIBLE);
                adapter.setCurrentList(eventos);
            } else if (state instanceof ResponseUiState.Failure) {
                binding.textNotResult.setVisibility(View.VISIBLE);
                binding.progressCircular.setVisibility(View.GONE);
                var erroMsg = ((ResponseUiState.Failure<List<PalestraDTO>>) state).getErroMessage();
                Toast.makeText(getActivity(), erroMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}