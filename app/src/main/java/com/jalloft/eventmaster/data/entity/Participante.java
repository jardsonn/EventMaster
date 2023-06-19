package com.jalloft.eventmaster.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.jalloft.eventmaster.data.dto.ParticipanteEventoDTO;

@Entity(tableName = "participantes")
public class Participante {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String nome;
    private String sobrenome;
    private String email;
    private String empresa;

    public Participante(String nome, String sobrenome, String email, String empresa) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.empresa = empresa;
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

    public ParticipanteEventoDTO participanteEventoDTO() {
        return new ParticipanteEventoDTO(id, nome, sobrenome, email, empresa);
    }
}
