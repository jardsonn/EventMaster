package com.jalloft.eventmaster.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jalloft.eventmaster.data.entity.Participante;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface ParticipanteDao extends BaseDao<Participante> {

    @Query("SELECT * FROM participantes WHERE id IN (SELECT participanteId FROM evento_participante WHERE eventoId = :eventoId)")
    Observable<List<Participante>> getParticipantesPorEventoId(long eventoId);
}
