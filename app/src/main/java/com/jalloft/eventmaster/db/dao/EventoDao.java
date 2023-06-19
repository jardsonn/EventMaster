package com.jalloft.eventmaster.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.jalloft.eventmaster.data.entity.Evento;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface EventoDao extends BaseDao<Evento> {

    @Query("SELECT * FROM eventos ORDER BY id DESC")
    Observable<List<Evento>> getEventos();

    @Query("SELECT nome FROM eventos WHERE id = :id")
    Single<String> getEventoNome(long id);

    @Query("SELECT data FROM eventos")
    Single<Date> getEventoDatas();
}
