package com.jalloft.eventmaster.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jalloft.eventmaster.data.entity.Sala;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SalaDao extends BaseDao<Sala> {
    @Query("SELECT * FROM salas WHERE id NOT IN (SELECT salaId FROM palestras WHERE horarioRealizacao = :horarioPalestra)")
    Observable<List<Sala>> getSalasDisponiveisPorHorario(Date horarioPalestra);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSala(Sala sala);

    @Query("SELECT * FROM salas")
    Observable<List<Sala>> getSalas();

    @Query("SELECT * FROM salas WHERE eventoId = :eventoId")
    Maybe<List<Sala>> getSalaPorEvento(long eventoId);

    //    @Query("SELECT * FROM salas WHERE id NOT IN (SELECT salaId FROM palestras WHERE eventoId = :eventoId)")
    @Query("SELECT * FROM salas " + "WHERE eventoId = :eventoId " + "AND NOT EXISTS " + "(SELECT 1 FROM palestras WHERE palestras.eventoId = :eventoId AND palestras.salaId = salas.id)")
    Maybe<List<Sala>> getSalasDisponiveisPorEvento(long eventoId);


    @Query("SELECT * FROM salas WHERE id NOT IN " + "(SELECT salaId FROM palestras " + "WHERE horarioRealizacao >= :startTime AND horarioRealizacao < :endTime)")
    Observable<List<Sala>> getSalasDisponiveis(Date startTime, Date endTime);

}
