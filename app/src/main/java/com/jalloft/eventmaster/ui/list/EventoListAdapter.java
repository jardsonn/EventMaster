package com.jalloft.eventmaster.ui.list;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.databinding.ItemEventsBinding;
import com.jalloft.eventmaster.utils.ColorUtils;
import com.jalloft.eventmaster.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.qualifiers.ActivityContext;

public class EventoListAdapter extends RecyclerView.Adapter<EventoListAdapter.EventoViewHolder> {

    private List<EventoDTO> currentList;
    private Observer<EventoDTO> observerEventClick;

    public EventoListAdapter() {
        this.currentList = new ArrayList<>();
    }

    public EventoListAdapter(List<EventoDTO> currentList) {
        this.currentList = currentList;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventoViewHolder(ItemEventsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        holder.onBind(currentList.get(position));
    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentList(List<EventoDTO> currentList) {
        this.currentList = currentList;
        notifyDataSetChanged();
    }

    public void addNewItem(EventoDTO evento) {
        if (currentList != null) {
            currentList.add(evento);
            notifyItemInserted(currentList.size() - 1);
        }
    }

    public void setOnEventClickListener(Observer<EventoDTO> observerEventClick) {
        this.observerEventClick = observerEventClick;
    }

    class EventoViewHolder extends RecyclerView.ViewHolder {

        private final ItemEventsBinding binding;
        private EventoDTO currentEvento;

        public EventoViewHolder(ItemEventsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                if (observerEventClick != null && currentEvento != null) {
                    observerEventClick.onChanged(currentEvento);
                }
            });
        }

        void onBind(EventoDTO evento) {
            this.currentEvento = evento;
            binding.getRoot().setCardBackgroundColor(ColorStateList.valueOf(ColorUtils.getColor(evento.getNome())));
            binding.eventTitle.setText(evento.getNome());
            binding.eventAddress.setText(evento.getLocal());
            binding.eventDate.setText(Utils.convertDate(evento.getData()));
        }
    }
}
