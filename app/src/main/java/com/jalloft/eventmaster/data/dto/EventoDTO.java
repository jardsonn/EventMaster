package com.jalloft.eventmaster.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.jalloft.eventmaster.data.entity.Evento;
import com.jalloft.eventmaster.data.entity.Sala;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class EventoDTO {
    private long id;
    private String nome;
    private Date data;
    private String local;

    private Collection<SalaDTO> salas;

    public EventoDTO() {
    }

    public EventoDTO(String nome) {
        this.nome = nome;
    }

    public EventoDTO(String nome, Date data, String local, Collection<SalaDTO> salas) {
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.salas = salas;
    }

    public EventoDTO(long id, String nome, Date data, String local) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.local = local;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Evento toEvento() {
        return new Evento(nome, data, local);
    }

    public Collection<SalaDTO> getSalas() {
        return salas;
    }

    public void setSalas(Collection<SalaDTO> salas) {
        this.salas = salas;
    }

    @NonNull
    @Override
    public String toString() {
        return nome;
    }
}
