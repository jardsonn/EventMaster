package com.jalloft.eventmaster.ui.list;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jalloft.eventmaster.data.dto.ParticipanteEventoDTO;
import com.jalloft.eventmaster.databinding.ItemParticipantsBinding;

import java.util.ArrayList;
import java.util.List;

public class ParticipanteListAdapter extends RecyclerView.Adapter<ParticipanteListAdapter.ParticipanteViewHolder> {

    private List<ParticipanteEventoDTO> currentList;

    public ParticipanteListAdapter() {
        this.currentList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<ParticipanteEventoDTO> currentList) {
        this.currentList = currentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ParticipanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParticipanteViewHolder(
                ItemParticipantsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteViewHolder holder, int position) {
        holder.onBind(currentList.get(position));
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    static class ParticipanteViewHolder extends RecyclerView.ViewHolder {

        private final ItemParticipantsBinding binding;

        public ParticipanteViewHolder(ItemParticipantsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(ParticipanteEventoDTO participante) {
            var empresaNome = participante.getEmpresa();
            binding.textParticipanteNome.setText(participante.getNome());
            binding.textParticipanteEmail.setText(participante.getEmail());
            if (empresaNome != null) {
                binding.textParticipanteEmpresa.setText(empresaNome);

            }
        }

    }
}
