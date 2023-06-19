package com.jalloft.eventmaster.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.jalloft.eventmaster.data.dto.EventoDTO;

import java.util.Date;

@Entity(tableName = "eventos")
public class Evento {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String nome;
    private Date data;
    private String local;

    public Evento(String nome, Date data, String local) {
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

    @Ignore
    public EventoDTO eventoDTO() {
        return new EventoDTO(id, nome, data, local);
    }
}
