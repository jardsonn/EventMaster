package com.jalloft.eventmaster.ui.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.data.entity.Sala;
import com.jalloft.eventmaster.databinding.ItemSalaPopupwindowBinding;

import java.util.List;

public class DialogSalasListAdapter extends RecyclerView.Adapter<DialogSalasListAdapter.PopWindowSalasViewHolder> {

    private List<SalaDTO> currentSalaList;

    public DialogSalasListAdapter(List<SalaDTO> currentSalaList) {
        this.currentSalaList = currentSalaList;
    }

    @NonNull
    @Override
    public PopWindowSalasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PopWindowSalasViewHolder(
                ItemSalaPopupwindowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PopWindowSalasViewHolder holder, int position) {
        holder.viewBind(currentSalaList.get(position));
    }

    @Override
    public int getItemCount() {
        return currentSalaList.size();
    }

    static class PopWindowSalasViewHolder extends RecyclerView.ViewHolder {

        private final ItemSalaPopupwindowBinding binding;

        public PopWindowSalasViewHolder(ItemSalaPopupwindowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void viewBind(SalaDTO sala) {
            System.out.println("PopWindowSalasViewHolder.viewBind " + sala.getNome());
            binding.salaCapacidadeWindow.setText(String.format(binding.getRoot().getContext().getText(R.string.capacidade_sala_atual).toString(), sala.getCapacidadeMaxima()));
            binding.salaNomeWindow.setText(sala.getNome());
        }

    }
}
