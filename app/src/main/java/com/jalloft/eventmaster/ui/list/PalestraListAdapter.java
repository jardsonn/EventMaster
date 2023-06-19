package com.jalloft.eventmaster.ui.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.PalestraDTO;
import com.jalloft.eventmaster.databinding.ItemLectureBinding;
import com.jalloft.eventmaster.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PalestraListAdapter extends RecyclerView.Adapter<PalestraListAdapter.PalestraViewHolder> {

    private List<PalestraDTO> currentList;

    public PalestraListAdapter() {
        this.currentList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<PalestraDTO> currentList) {
        this.currentList = currentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PalestraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PalestraViewHolder(
                ItemLectureBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PalestraViewHolder holder, int position) {
        holder.onBind(currentList.get(position));
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    class PalestraViewHolder extends RecyclerView.ViewHolder {

        private final ItemLectureBinding binding;
        private final Context context;

        public PalestraViewHolder(ItemLectureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
        }

        public void onBind(PalestraDTO palestra) {
            binding.textTitulo.setText(palestra.getTitulo());
            binding.textPalestante.setText(palestra.getNomePalestrante());
            binding.textEventoName.setText(palestra.getEventoNome());
            binding.textEventoDataRealizacao.setText(Utils.convertDateAndTime(palestra.getHorarioRealizacao()));
            var bg = Utils.colorLectureBg(context, palestra.getEventoNome());
            binding.lectureBg.setImageDrawable(bg);
        }
    }
}
