package com.jalloft.eventmaster.data.dto;

import androidx.annotation.NonNull;

import com.jalloft.eventmaster.data.entity.Sala;

public class SalaDTO {
    private String nome;
    private int capacidadeMaxima;
    private String eventoName;
    private long id;

    private long eventoId;

    public SalaDTO(String nome) {
        this.nome = nome;
    }

    public SalaDTO(String nome, int capacidadeMaxima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public SalaDTO(long eventoId, String nome, int capacidadeMaxima) {
        this.eventoId = eventoId;
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
    }

  public SalaDTO(long id, long eventoId, String nome, int capacidadeMaxima) {
        this.id = id;
        this.eventoId = eventoId;
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Sala toSala(long eventId) {
        return new Sala(eventId, nome, capacidadeMaxima);
    }

  public Sala toSala() {
        return new Sala(eventoId, nome, capacidadeMaxima);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventoName() {
        return eventoName;
    }

    public void setEventoName(String eventoName) {
        this.eventoName = eventoName;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    @NonNull
    @Override
    public String toString() {
        return nome;
    }
}
