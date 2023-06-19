package com.jalloft.eventmaster.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.data.entity.Sala;
import com.jalloft.eventmaster.databinding.DialogAddNewRoomBinding;
import com.jalloft.eventmaster.databinding.FragmentNewEventBinding;
import com.jalloft.eventmaster.databinding.PopupWindowSalaLayoutBinding;
import com.jalloft.eventmaster.ui.MainActivity;
import com.jalloft.eventmaster.ui.list.DialogSalasListAdapter;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;
import com.jalloft.eventmaster.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class NewEventFragment extends Fragment {

    private FragmentNewEventBinding binding;
    private EventoViewModel viewModel;

    private NavController navController;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewEventBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var activity = (MainActivity) requireActivity();
        navController = NavHostFragment.findNavController(this);
        binding.toolbarNewEvent.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });

        binding.eventDateEdit.setOnClickListener(v -> {
            createDatePicker();
            binding.eventNameEdit.clearFocus();
            binding.eventLocationEdit.clearFocus();
        });


        binding.btnSaveEvent.setOnClickListener(v -> {
            String name = binding.eventNameEdit.getText().toString();
            String location = binding.eventLocationEdit.getText().toString();
            Date date = viewModel.currentEditionDate.getValue();
            var evento = new EventoDTO(name, date, location, viewModel.currentEditionSalas.getValue());
            viewModel.salvarEvento(evento);
        });

        binding.btnAddRoom.setOnClickListener(v -> {
            createDialogRoom();
        });

        binding.eventRooms.setOnClickListener(v -> {
            if (viewModel.currentEditionSalas.getValue() != null && !viewModel.currentEditionSalas.getValue().isEmpty()) {
                createAlertDialog();
            }
        });

        setupObservers();
    }

    private void createAlertDialog() {
        var windowBinding = PopupWindowSalaLayoutBinding.inflate(getLayoutInflater());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        windowBinding.rvSalas.setLayoutManager(layoutManager);
        DialogSalasListAdapter adapter = new DialogSalasListAdapter(viewModel.currentEditionSalas.getValue());
        windowBinding.rvSalas.setAdapter(adapter);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(windowBinding.getRoot()).create();
        windowBinding.btnClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void createDialogRoom() {
        var dialogViewBinding = DialogAddNewRoomBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogViewBinding.getRoot()).create();
        dialogViewBinding.btnDialogCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogViewBinding.btnDialogConfirm.setOnClickListener(v -> {
            var salaNome = dialogViewBinding.salaNomeEdit.getText();
            var salaCapacidade = dialogViewBinding.salaCapacidadeEdit.getText();
            if (!TextUtils.isEmpty(salaNome) && !TextUtils.isEmpty(salaCapacidade)) {
                try {
                    var sala = new SalaDTO(salaNome.toString(), Integer.parseInt(salaCapacidade.toString()));
                    var list = viewModel.currentEditionSalas.getValue();
                    if (list != null) {
                        list.add(sala);
                        viewModel.currentEditionSalas.setValue(list);
                    }

                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setupObservers() {
        viewModel.currentEditionSalas.observe(getViewLifecycleOwner(), salas -> {
            binding.eventRooms.setText(salas.isEmpty() ? getString(R.string.nenhuma_sala) : getString(R.string.salas_eventos, salas.size()));
        });

        viewModel.currentEditionDate.observe(getViewLifecycleOwner(), date -> {
            if (date != null){
                binding.eventDateEdit.setText(Utils.convertDate(date));
            }else {
                binding.eventDateEdit.setText(R.string.digite_a_data_evento);
            }
        });
        viewModel.cadastrarEventoState.observe(getViewLifecycleOwner(), eventState -> {
            if (eventState != null) {
                if (eventState instanceof ResponseUiState.Loading) {
                    changeLoadingUiState(true);
                } else if (eventState instanceof ResponseUiState.Success) {
                    var successDataId = ((ResponseUiState.Success<Integer>) eventState).getData();
                    Toast.makeText(requireContext(), successDataId, Toast.LENGTH_SHORT).show();
                    if (navController != null) {
                        navController.popBackStack();
                    }
                    changeLoadingUiState(false);
                } else if (eventState instanceof ResponseUiState.Failure) {
                    var erroMessageId = ((ResponseUiState.Failure<Integer>) eventState).getErroMessage();
                    Toast.makeText(requireContext(), erroMessageId, Toast.LENGTH_SHORT).show();
                    changeLoadingUiState(false);
                }
            }
        });
    }

    private void changeLoadingUiState(boolean isVisibility) {
        binding.btnSaveEvent.setVisibility(isVisibility ? View.GONE : View.VISIBLE);
        binding.progressCircular.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
    }

    private void createDatePicker() {
        CalendarConstraints constraints = getCalendarConstraints();
        Date currentDate = viewModel.currentEditionDate.getValue();
        var dateRangePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                .setTitleText(R.string.definir_a_data)
                .setSelection(currentDate != null ? currentDate.getTime() : new Date(System.currentTimeMillis()).getTime())
                .setCalendarConstraints(constraints)
                .build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            viewModel.currentEditionDate.setValue(new Date(selection));
            System.out.println("NewEventFragment.createDatePicker selection " + selection);
        });
        dateRangePicker.show(requireActivity().getSupportFragmentManager(), "MaterialDatePicker");
    }

    private CalendarConstraints getCalendarConstraints() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.clear();
        calendar.set(currentYear, currentMonth, currentDayOfMonth, 0, 0, 0);

        long startOfToday = calendar.getTimeInMillis();
        return new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .setStart(startOfToday)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.cadastrarEventoState.setValue(null);
        viewModel.currentEditionSalas.setValue(new ArrayList<>());
        viewModel.currentEditionDate.setValue(null);
        binding = null;
    }
}
