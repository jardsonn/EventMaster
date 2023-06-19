package com.jalloft.eventmaster.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
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

import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.ParticipanteEventoDTO;
import com.jalloft.eventmaster.databinding.DialogAddNewParticipantBinding;
import com.jalloft.eventmaster.databinding.DialogAddNewRoomBinding;
import com.jalloft.eventmaster.databinding.FragmentShowEventBinding;
import com.jalloft.eventmaster.ui.MainActivity;
import com.jalloft.eventmaster.ui.list.ParticipanteListAdapter;
import com.jalloft.eventmaster.ui.states.ResponseUiState;
import com.jalloft.eventmaster.ui.viewmodel.EventoViewModel;
import com.jalloft.eventmaster.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShowEventFragment extends Fragment {

    private FragmentShowEventBinding binding;
    private EventoViewModel viewModel;
    private EventoDTO currentEvento;

    private NavController navController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShowEventBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(EventoViewModel.class);
        var args = getArguments();
        if (args != null) {
            long eventId = ShowEventFragmentArgs.fromBundle(args).getCurrentEventId();
            var events = viewModel.events.getValue();
            if (events != null) {
                var currentEvento = events.stream().filter(evento -> evento.getId() == eventId).findFirst();
                currentEvento.ifPresent(eventoDTO -> this.currentEvento = eventoDTO);
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var activity = (MainActivity) requireActivity();
        navController = NavHostFragment.findNavController(this);
        binding.toolbarShowEvent.setNavigationOnClickListener(v -> {
            navController.popBackStack();
        });
        if (currentEvento == null) {
            navController.popBackStack();
            return;
        } else {
            binding.toolbarShowEvent.setTitle(currentEvento.getNome());
            binding.textData.setText(Utils.convertDate(currentEvento.getData()));
            binding.textLocal.setText(currentEvento.getLocal());
            viewModel.carregarListaParticipantes(currentEvento.getId());

            ParticipanteListAdapter adapter = new ParticipanteListAdapter();
            binding.rvParticipantes.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvParticipantes.setAdapter(adapter);
            binding.rvParticipantes.setHasFixedSize(true);

            binding.btnFabAddParticipant.setOnClickListener(v -> {
                createDialogNewParticipant();
            });

            viewModel.participantesListUiState.observe(getViewLifecycleOwner(), participantesState -> {
                if (participantesState instanceof ResponseUiState.Loading) {
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    binding.textEmpty.setVisibility(View.GONE);
                } else if (participantesState instanceof ResponseUiState.Success) {
                    binding.progressCircular.setVisibility(View.GONE);
                    var eventos = ((ResponseUiState.Success<List<ParticipanteEventoDTO>>) participantesState).getData();
                    binding.textEmpty.setVisibility(!eventos.isEmpty() ? View.GONE : View.VISIBLE);
                    adapter.setCurrentList(eventos);
                } else if (participantesState instanceof ResponseUiState.Failure) {
                    binding.textEmpty.setVisibility(View.VISIBLE);
                    binding.progressCircular.setVisibility(View.GONE);
                    var erroMsg = ((ResponseUiState.Failure<List<ParticipanteEventoDTO>>) participantesState).getErroMessage();
                    Toast.makeText(activity, erroMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createDialogNewParticipant() {
        var dialogViewBinding = DialogAddNewParticipantBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogViewBinding.getRoot()).create();
        dialogViewBinding.btnDialogCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogViewBinding.btnDialogConfirm.setOnClickListener(v -> {
            Editable nome = dialogViewBinding.participanteNomeEdit.getText();
            Editable sobrenome = dialogViewBinding.participanteSobrenomeEdit.getText();
            Editable email = dialogViewBinding.participanteEmailEdit.getText();
            Editable empresa = dialogViewBinding.participanteEmpresaEdit.getText();
            if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(sobrenome) && !TextUtils.isEmpty(email)) {
                var participanteEvento = new ParticipanteEventoDTO(nome.toString(), sobrenome.toString(), email.toString(), TextUtils.isEmpty(empresa) ? null : empresa.toString(), currentEvento.getId());
                viewModel.salvarParticipante(participanteEvento);
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Existe campos obrigatórios vazio", Toast.LENGTH_SHORT).show();
            }

        });
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
