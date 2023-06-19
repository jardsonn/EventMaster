package com.jalloft.eventmaster.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jalloft.eventmaster.data.dto.PalestraDTO;

import java.util.Date;

@Entity(tableName = "palestras", foreignKeys = @ForeignKey(entity = Evento.class, parentColumns = "id", childColumns = "eventoId", onDelete = ForeignKey.CASCADE))
public class Palestra {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long eventoId;
    private long salaId;
    private String titulo;
    private String nomePalestrante;
    private Date horarioRealizacao;

    public Palestra(long eventoId, long salaId, String titulo, String nomePalestrante, Date horarioRealizacao) {
        this.eventoId = eventoId;
        this.salaId = salaId;
        this.titulo = titulo;
        this.nomePalestrante = nomePalestrante;
        this.horarioRealizacao = horarioRealizacao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNomePalestrante() {
        return nomePalestrante;
    }

    public void setNomePalestrante(String nomePalestrante) {
        this.nomePalestrante = nomePalestrante;
    }

    public Date getHorarioRealizacao() {
        return horarioRealizacao;
    }

    public void setHorarioRealizacao(Date data) {
        this.horarioRealizacao = data;
    }

    public long getSalaId() {
        return salaId;
    }

    public void setSalaId(long salaId) {
        this.salaId = salaId;
    }

    @Ignore
    public PalestraDTO palestraDTO() {
        return new PalestraDTO(eventoId, salaId, titulo, nomePalestrante, horarioRealizacao);
    }
}
