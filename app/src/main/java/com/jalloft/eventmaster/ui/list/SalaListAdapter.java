package com.jalloft.eventmaster.ui.list;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.databinding.ItemRoomBinding;
import com.jalloft.eventmaster.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SalaListAdapter extends RecyclerView.Adapter<SalaListAdapter.SalaViewHolder> {

    private List<SalaDTO> currentList;

    public SalaListAdapter() {
        this.currentList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<SalaDTO> currentList) {
        this.currentList = currentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SalaViewHolder(
                ItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SalaViewHolder holder, int position) {
        holder.onBind(currentList.get(position));
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    class SalaViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoomBinding binding;

        public SalaViewHolder(ItemRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(SalaDTO sala) {
            binding.textNome.setText(sala.getNome());
            binding.textCapacidadeMax.setText(String.valueOf(sala.getCapacidadeMaxima()));
            binding.textEventoNome.setText(sala.getEventoName());

            binding.roomBg.setImageDrawable(Utils.colorLectureBg(binding.getRoot().getContext(), sala.getEventoName()));
        }

    }
}
