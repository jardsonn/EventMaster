package com.jalloft.eventmaster.ui.fragments;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.PalestraDTO;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.databinding.FragmentNewLectureBinding;
import com.jalloft.eventmaster.ui.MainActivity;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;
import com.jalloft.eventmaster.utils.MyTextWatcher;
import com.jalloft.eventmaster.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewLectureFragment extends Fragment {

    private FragmentNewLectureBinding binding;
    private NavController navController;
    private EventoViewModel viewModel;
    private int currentInitialHour = 8;
    private int currentInitialMinute = 0;

    private final PalestraDTO currentPalestra = new PalestraDTO();
    private EventoDTO currentEvento = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewLectureBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var activity = (MainActivity) requireActivity();
        navController = NavHostFragment.findNavController(this);
        binding.toolbarNewLecture.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });

        setUpSpinnerEvento();
        viewModel.salasDisponiveis.observe(getViewLifecycleOwner(), this::setUpSpinnerSala);

        binding.textPalestraHorarioHora.setOnClickListener(v -> {
            createTimePicker((TextView) v);
        });

        binding.editPalestraTitulo.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    currentPalestra.setTitulo(s.toString());
                }
            }
        });

        binding.editPalestraNomePalestrante.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    currentPalestra.setNomePalestrante(s.toString());
                }
            }
        });

        viewModel.cadastrarPalestraState.observe(getViewLifecycleOwner(), state -> {
            if (state != null) {
                if (state instanceof ResponseUiState.Loading) {
                    changeLoadingUiState(true);
                } else if (state instanceof ResponseUiState.Success) {
                    var successDataId = ((ResponseUiState.Success<Integer>) state).getData();
                    Toast.makeText(requireContext(), successDataId, Toast.LENGTH_SHORT).show();
                    if (navController != null) {
                        navController.popBackStack();
                    }
                    changeLoadingUiState(false);
                } else if (state instanceof ResponseUiState.Failure) {
                    var erroMessageId = ((ResponseUiState.Failure<Integer>) state).getErroMessage();
                    Toast.makeText(requireContext(), erroMessageId, Toast.LENGTH_SHORT).show();
                    changeLoadingUiState(false);
                }
            }
        });

        binding.btnSalvarPalestra.setOnClickListener(v -> {
            viewModel.salvarPalestra(currentPalestra);
        });
    }

    private void changeLoadingUiState(boolean isVisibility) {
        binding.btnSalvarPalestra.setVisibility(isVisibility ? View.GONE : View.VISIBLE);
        binding.progressCircular.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
    }

    private void createTimePicker(TextView text) {
        var picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(currentInitialHour)
                .setMinute(currentInitialMinute)
                .setInputMode(INPUT_MODE_CLOCK)
                .setTitleText("Selecione a hora de início")
                .build();

        picker.addOnPositiveButtonClickListener(view -> {
            currentInitialHour = picker.getHour();
            currentInitialMinute = picker.getMinute();
            text.setText(String.format(Locale.getDefault(), "%02d:%02d às %02d:%02d", currentInitialHour, currentInitialMinute, currentInitialHour + 2, currentInitialMinute));

            Calendar calendar = Calendar.getInstance();
            if (currentEvento != null && currentEvento.getData() != null) {
                calendar.setTime(currentEvento.getData());
                calendar.set(Calendar.HOUR_OF_DAY, currentInitialHour);
                calendar.set(Calendar.MINUTE, currentInitialMinute);

                Date selectedDateTime = calendar.getTime();
                currentPalestra.setHorarioRealizacao(selectedDateTime);
            }
        });

        picker.show(requireActivity().getSupportFragmentManager(), "MaterialTimePicker.Time");


    }

    private void setUpSpinnerSala(List<SalaDTO> salas) {
        List<SalaDTO> roomOptions = new ArrayList<>();
        roomOptions.add(new SalaDTO(getString(R.string.nao_definido)));
        if (salas != null) {
            roomOptions.addAll(salas);
        }
        ArrayAdapter<SalaDTO> adapterRoom = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roomOptions);
        adapterRoom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPalestraSala.setEnabled(salas == null || !salas.isEmpty());
        binding.spinnerPalestraSala.setAdapter(adapterRoom);
        binding.spinnerPalestraSala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (pos != 0) {
                    SalaDTO sala = (SalaDTO) adapterView.getItemAtPosition(pos);
                    currentPalestra.setSalaId(sala.getId());
                } else {
                    currentPalestra.setSalaId(-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpSpinnerEvento() {
        var events = viewModel.events.getValue();
        if (events != null) {
            List<EventoDTO> eventOptions = new ArrayList<>();
            eventOptions.add(new EventoDTO(getString(R.string.nao_definido)));
            eventOptions.addAll(events);
            ArrayAdapter<EventoDTO> adapterEvent = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, eventOptions);
            adapterEvent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerPalestraEvento.setAdapter(adapterEvent);
            binding.spinnerPalestraEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    EventoDTO evento = (EventoDTO) adapterView.getItemAtPosition(pos);
                    currentEvento = evento;
                    currentPalestra.setEventoId(evento.getData() == null ? -1 : evento.getId());
                    viewModel.getSalasDisponiveisPorEvento(evento.getId());
                    binding.textPalestraHorarioHora.setEnabled(evento.getData() != null);
                    binding.textPalestraHorarioDia.setText(evento.getData() != null ? Utils.convertDate(evento.getData()) : getString(R.string.nao_definido));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
