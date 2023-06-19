package com.jalloft.eventmaster.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "evento_participante",
        primaryKeys = { "eventoId", "participanteId" },
        foreignKeys = {
                @ForeignKey(entity = Evento.class, parentColumns = "id", childColumns = "eventoId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Participante.class, parentColumns = "id", childColumns = "participanteId", onDelete = ForeignKey.CASCADE)
        })
public class EventoParticipante {
    private long eventoId;
    private long participanteId;

    public EventoParticipante(long eventoId, long participanteId) {
        this.eventoId = eventoId;
        this.participanteId = participanteId;
    }

    public long getEventoId() {
        return eventoId;
    }

    public void setEventoId(long eventoId) {
        this.eventoId = eventoId;
    }

    public long getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(long participanteId) {
        this.participanteId = participanteId;
    }
}
