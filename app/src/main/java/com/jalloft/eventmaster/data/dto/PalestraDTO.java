package com.jalloft.eventmaster.data.dto;

import com.jalloft.eventmaster.data.entity.Palestra;

import java.util.Date;

public class PalestraDTO {

    private long eventoId;
    private long salaId;
    private String titulo;
    private String nomePalestrante;
    private Date horarioRealizacao;

    private String eventoNome;

    public PalestraDTO() {
    }

    public PalestraDTO(long eventoId, long salaId, String titulo, String nomePalestrante, Date horarioRealizacao) {
        this.eventoId = eventoId;
        this.salaId = salaId;
        this.titulo = titulo;
        this.nomePalestrante = nomePalestrante;
        this.horarioRealizacao = horarioRealizacao;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    public long getSalaId() {
        return salaId;
    }

    public void setSalaId(long salaId) {
        this.salaId = salaId;
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

    public void setHorarioRealizacao(Date horarioRealizacao) {
        this.horarioRealizacao = horarioRealizacao;
    }

    public String getEventoNome() {
        return eventoNome;
    }

    public void setEventoNome(String eventoNome) {
        this.eventoNome = eventoNome;
    }

    public Palestra toPalestra(){
        return new Palestra(eventoId, salaId, titulo, nomePalestrante, horarioRealizacao);
    }
}
