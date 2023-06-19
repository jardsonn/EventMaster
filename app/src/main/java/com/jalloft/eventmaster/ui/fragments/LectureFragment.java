package com.jalloft.eventmaster.ui.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.PalestraDTO;
import com.jalloft.eventmaster.databinding.DialogInformationBinding;
import com.jalloft.eventmaster.databinding.FragmentLectureBinding;
import com.jalloft.eventmaster.ui.MainActivity;
import com.jalloft.eventmaster.ui.list.PalestraListAdapter;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LectureFragment extends Fragment implements MenuProvider, OnNewClickListener {

    private FragmentLectureBinding binding;
    private EventoViewModel viewModel;

    private PalestraListAdapter adapter;

    private MainActivity mainActivity;

    private int indexMenuEvent = 0;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLectureBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        requireActivity().addMenuProvider(this);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        binding.includeEmptyLayout.btnFirstEvent.setText(R.string.cadastrar_nova_palesta);
        binding.includeEmptyLayout.textEmptyList.setText(R.string.nenhum_palestra_cadastrada);

        adapter = new PalestraListAdapter();
        binding.rvLecture.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvLecture.hasFixedSize();
        binding.rvLecture.setAdapter(adapter);

        mainActivity = ((MainActivity) requireActivity());

//        binding.includeEmptyLayout.btnFirstEvent.setOnClickListener(button -> {
//            addNewLecture(mainActivity);
//        });

        viewModel.palestrasListUiState.observe(getViewLifecycleOwner(), palestraState -> {
            if (palestraState instanceof ResponseUiState.Loading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            } else if (palestraState instanceof ResponseUiState.Success) {
                binding.progressCircular.setVisibility(View.GONE);
                var eventos = ((ResponseUiState.Success<List<PalestraDTO>>) palestraState).getData();
                binding.includeEmptyLayout.getRoot().setVisibility(!eventos.isEmpty() ? View.GONE : View.VISIBLE);
                adapter.setCurrentList(eventos);
            } else if (palestraState instanceof ResponseUiState.Failure) {
                binding.progressCircular.setVisibility(View.GONE);
                var erroMsg = ((ResponseUiState.Failure<List<PalestraDTO>>) palestraState).getErroMessage();
                Toast.makeText(mainActivity, erroMsg, Toast.LENGTH_SHORT).show();
            }
        });

//        binding.btnAddLecture.setOnClickListener(v -> {
//            addNewLecture(mainActivity);
//        });
    }

    public void addNewLecture(MainActivity activity) {
        var currentFragment = activity.getCurrentMainFragment();

        var existeEventoCadastrada = viewModel.existeEventoCadastrada.getValue();
        if (existeEventoCadastrada != null && existeEventoCadastrada) {
            if (currentFragment instanceof MainOptionsFragment) {
                ((MainOptionsFragment) currentFragment).findNavController().navigate(MainOptionsFragmentDirections.actionNavigationMainOptionsToNavigationNewLecture());
            }
        } else {
            createInfoSalaDialog(
                    R.string.nao_possivel_adicionar_palestras_titulo,
                    R.string.nao_possivel_adicionar_palestras_message,
                    R.string.cancelar,
                    R.string.cadastrar_evento,
                    v -> {
                        activity.irParaAdicionarNovoEvento();
                    }
            );
        }
    }

    private void createInfoSalaDialog(@StringRes int title, @StringRes int message, @StringRes int negativeButtonText, @StringRes int positiveButtonText, View.OnClickListener clickListener) {
        var dialogViewBinding = DialogInformationBinding.inflate(getLayoutInflater());
        dialogViewBinding.dialogTitle.setText(title);
        dialogViewBinding.dialogMessage.setText(message);
        dialogViewBinding.btnDialogCancel.setText(negativeButtonText);
        dialogViewBinding.btnDialogConfirm.setText(positiveButtonText);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogViewBinding.getRoot()).create();
        dialogViewBinding.btnDialogCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogViewBinding.btnDialogConfirm.setOnClickListener(v -> {
            clickListener.onClick(v);
            dialog.dismiss();
        });

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(this);
        binding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.action_menu_lecture_filter, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_menu_filter_by_events) {
            createEventsDialog();
            return true;
        }
        return false;
    }

    private void createEventsDialog() {
        var events = viewModel.events.getValue();
        if (events != null) {
            Collections.reverse(events);
            CharSequence[] eventsNames = new CharSequence[events.size() + 1];
            eventsNames[0] = "Todas palestras";
            for (int i = 1; i < events.size() + 1; i++) {
                eventsNames[i] = events.get(i - 1).getNome();
            }
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.eventos)
                    .setSingleChoiceItems(eventsNames, indexMenuEvent, (dialogInterface, i) -> {
                        indexMenuEvent = i;
                    })
                    .setPositiveButton(R.string.escolher, (dialogInterface, i) -> {
                        int realIndex = indexMenuEvent - 1;
                        viewModel.carregarListaPalestras(realIndex != -1 ? events.get(realIndex).getId() : realIndex);
                    })
                    .create();
            dialog.show();
        }
    }

    @Override
    public void onAddClick() {
        addNewLecture(mainActivity);
    }
}