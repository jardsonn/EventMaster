package com.jalloft.eventmaster.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jalloft.eventmaster.data.dto.SalaDTO;

@Entity(
        tableName = "salas",
        foreignKeys = @ForeignKey(entity = Evento.class,
                parentColumns = "id",
                childColumns = "eventoId",
                onDelete = ForeignKey.CASCADE)
)
public class Sala {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long eventoId;
    private String nome;
    private int capacidadeMaxima;

    public Sala(long eventoId, String nome, int capacidadeMaxima) {
        this.eventoId = eventoId;
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
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

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    @Ignore
    public SalaDTO toSalaDTO(){
        return new SalaDTO(id, eventoId, nome, capacidadeMaxima);
    }
}
