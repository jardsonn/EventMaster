package com.jalloft.eventmaster.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.databinding.ChipItemBinding;
import com.jalloft.eventmaster.databinding.DialogAddNewRoomBinding;
import com.jalloft.eventmaster.databinding.DialogTimeRoomPickerBinding;
import com.jalloft.eventmaster.databinding.FragmentRoomBinding;
import com.jalloft.eventmaster.ui.MainActivity;
import com.jalloft.eventmaster.ui.list.SalaListAdapter;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;
import com.jalloft.eventmaster.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RoomFragment extends Fragment implements MenuProvider, OnNewClickListener {

    private FragmentRoomBinding binding;
    private EventoViewModel viewModel;
    private SalaListAdapter adapter;

    private MainActivity mainActivity;
    private long currentEventoId = -1;

    private AlertDialog dialogNewRoom;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRoomBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        requireActivity().addMenuProvider(this);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        binding.includeEmptyLayout.btnFirstEvent.setText(R.string.cadastrar_nova_sala);
        binding.includeEmptyLayout.textEmptyList.setText(R.string.nenhum_sala_cadastrada);

        adapter = new SalaListAdapter();
        binding.rvRoom.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRoom.hasFixedSize();
        binding.rvRoom.setAdapter(adapter);

        mainActivity = ((MainActivity) requireActivity());


//        binding.includeEmptyLayout.btnFirstEvent.setOnClickListener(button -> {
//            addNewLecture(activity);
//        });
//        binding.btnAddRoom.setOnClickListener(v -> {
////            addNewLecture(activity);
//        });


        viewModel.salasListUiState.observe(getViewLifecycleOwner(), palestraState -> {
            if (palestraState instanceof ResponseUiState.Loading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            } else if (palestraState instanceof ResponseUiState.Success) {
                binding.progressCircular.setVisibility(View.GONE);
                var eventos = ((ResponseUiState.Success<List<SalaDTO>>) palestraState).getData();
                binding.includeEmptyLayout.getRoot().setVisibility(!eventos.isEmpty() ? View.GONE : View.VISIBLE);
                adapter.setCurrentList(eventos);
            } else if (palestraState instanceof ResponseUiState.Failure) {
                binding.progressCircular.setVisibility(View.GONE);
                var erroMsg = ((ResponseUiState.Failure<List<SalaDTO>>) palestraState).getErroMessage();
                Toast.makeText(mainActivity, erroMsg, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.cadastrarSalaState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ResponseUiState.Failure){
                Toast.makeText(requireContext(), ((ResponseUiState.Failure<Integer>) state).getErroMessage(), Toast.LENGTH_SHORT).show();
            }else if (state instanceof ResponseUiState.Success){
                if (dialogNewRoom != null && dialogNewRoom.isShowing()){
                    dialogNewRoom.dismiss();
                    dialogNewRoom = null;
                }
                Toast.makeText(requireContext(), ((ResponseUiState.Success<Integer>) state).getData(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().removeMenuProvider(this);
        viewModel.cadastrarSalaState.setValue(null);
        binding = null;
    }

    @Override
    public void onAddClick() {
        dialogNewRoom = createDialogNewRoom();
    }

    private AlertDialog createDialogNewRoom() {
        var dialogViewBinding = DialogAddNewRoomBinding.inflate(getLayoutInflater());
        setUpSpinnerEvento(dialogViewBinding);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogViewBinding.getRoot()).create();
        dialogViewBinding.btnDialogCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogViewBinding.btnDialogConfirm.setOnClickListener(v -> {
            var salaNome = dialogViewBinding.salaNomeEdit.getText();
            var salaCapacidade = dialogViewBinding.salaCapacidadeEdit.getText();
            try {
                var sala = new SalaDTO(currentEventoId, salaNome.toString(), TextUtils.isEmpty(salaCapacidade) ? 0 : Integer.parseInt(salaCapacidade.toString()));
                viewModel.salvarSala(sala);
//                    var list = viewModel.currentEditionSalas.getValue();
//                    if (list != null) {
//                        list.add(sala);
//                        viewModel.currentEditionSalas.setValue(list);
//                    }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return dialog;
    }

    private void setUpSpinnerEvento(DialogAddNewRoomBinding dialogBinding) {
        var events = viewModel.events.getValue();
        if (events != null) {
            List<EventoDTO> eventOptions = new ArrayList<>();
            eventOptions.add(new EventoDTO(getString(R.string.nao_definido)));
            eventOptions.addAll(events);
            ArrayAdapter<EventoDTO> adapterEvent = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventOptions);
            adapterEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dialogBinding.textSalaEvento.setVisibility(View.VISIBLE);
            dialogBinding.spinnerEventoContainer.setVisibility(View.VISIBLE);
            dialogBinding.spinnerEvento.setAdapter(adapterEvent);
            dialogBinding.spinnerEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    EventoDTO evento = (EventoDTO) adapterView.getItemAtPosition(pos);
                    currentEventoId = pos != 0 ? evento.getId() : -1;
//                    currentPalestra.setEventoId(evento.getData() == null ? -1 : evento.getId());
//                    viewModel.getSalasDisponiveisPorEvento(evento.getId());
//                    binding.textPalestraHorarioHora.setEnabled(evento.getData() != null);
//                    binding.textPalestraHorarioDia.setText(evento.getData() != null ? Utils.convertDate(evento.getData()) : getString(R.string.nao_definido));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.action_menu_room_date, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_menu_select_date) {
            createTimePicker();
            return true;
        }
        return false;
    }

    private void createTimePicker() {
        List<Date> dateList = viewModel.listaDeDatas.getValue();
        if (dateList != null) {
            var dialogViewBinding = DialogTimeRoomPickerBinding.inflate(getLayoutInflater());
            AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogViewBinding.getRoot()).create();
            dialogViewBinding.timePicker.setEnabled(false);

            for (int i = 0; i < dateList.size(); i++) {
                var date = dateList.get(i);
                var chip = createChip(Utils.convertDate(date, Utils.DATE_PATTERN_DMMYYYY), dialogViewBinding.chipGroup, i);
                dialogViewBinding.chipGroup.addView(chip);
            }

            dialogViewBinding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                dialogViewBinding.btnEscolher.setEnabled(!checkedIds.isEmpty());
                dialogViewBinding.timePicker.setEnabled(!checkedIds.isEmpty());

            });
            dialogViewBinding.btnCancelar.setOnClickListener(view -> dialog.dismiss());

            dialogViewBinding.btnEscolher.setOnClickListener(v -> {
                int horas = dialogViewBinding.timePicker.getHour();
                int minutos = dialogViewBinding.timePicker.getMinute();
                int checkedIndex = dialogViewBinding.chipGroup.getCheckedChipId();
                Date dia = dateList.get(checkedIndex);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dia);
                calendar.set(Calendar.HOUR_OF_DAY, horas);
                calendar.set(Calendar.MINUTE, minutos);
                Date selectedDateTime = calendar.getTime();
                calendar.add(Calendar.HOUR_OF_DAY, 2);
                Date endDate = calendar.getTime();
                //29 de junho
                //8:30 as 10:30
                viewModel.carregarListaSalas(selectedDateTime, endDate);
                System.out.println("RoomFragment.createTimePicker horas " + selectedDateTime.getTime());
                dialog.dismiss();
            });


            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }


    private Chip createChip(String text, ViewGroup parent, int id) {
        var chipBinding = ChipItemBinding.inflate(getLayoutInflater(), parent, false);
        var chip = chipBinding.getRoot();
        chip.setId(id);
        chip.setText(text);
        return chip;
    }
}