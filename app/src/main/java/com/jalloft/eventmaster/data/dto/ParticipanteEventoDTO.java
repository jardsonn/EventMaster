package com.jalloft.eventmaster.data.dto;

import com.jalloft.eventmaster.data.entity.Participante;

public class ParticipanteEventoDTO {

    private long participanteId;
    private long eventoId;
    private String nome;
    private String sobrenome;
    private String email;
    private String empresa;

    public ParticipanteEventoDTO(String nome, String sobrenome, String email, String empresa, long eventoId) {
        this.eventoId = eventoId;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.empresa = empresa;
    }
 public ParticipanteEventoDTO(long participanteId, String nome, String sobrenome, String email, String empresa) {
        this.participanteId = participanteId;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.empresa = empresa;
    }

    public long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(long participanteId) {
        this.participanteId = participanteId;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Participante toParticipante() {
        return new Participante(nome, sobrenome, email, empresa);
    }

}
