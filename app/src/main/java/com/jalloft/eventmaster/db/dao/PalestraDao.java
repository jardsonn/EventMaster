package com.jalloft.eventmaster.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jalloft.eventmaster.data.entity.Palestra;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PalestraDao extends BaseDao<Palestra> {
    @Query("SELECT * FROM palestras WHERE eventoId = :eventoId")
    Observable<List<Palestra>> getPalestrasPorEventoId(long eventoId);

    @Query("SELECT * FROM palestras WHERE nomePalestrante LIKE '%' || :nomePalestrante || '%'")
    Observable<List<Palestra>> getPalestrasPorPalestrante(String nomePalestrante);

    @Query("SELECT * FROM palestras")
    Observable<List<Palestra>> getPalestras();

    @Query("SELECT * FROM palestras WHERE titulo LIKE '%' || :titulo || '%'")
    Observable<List<Palestra>> getPalestrasPorTitulo(String titulo);
}
