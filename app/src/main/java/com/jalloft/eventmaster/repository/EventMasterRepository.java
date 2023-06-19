package com.jalloft.eventmaster.repository;


import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.data.entity.Evento;
import com.jalloft.eventmaster.data.entity.EventoParticipante;
import com.jalloft.eventmaster.data.entity.Palestra;
import com.jalloft.eventmaster.data.entity.Participante;
import com.jalloft.eventmaster.data.entity.Sala;
import com.jalloft.eventmaster.data.dto.ParticipanteEventoDTO;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface EventMasterRepository {

    Observable<List<Evento>> getEventos();

    Single<String> getEventoNome(long id);

    Observable<List<Palestra>> getPalestras();

    //    Single<Integer> salvarEvento(EventoDTO evento);
    Completable salvarEvento(EventoDTO evento);

    Completable salvarSala(Sala sala);

    Completable salvarSalas(Collection<SalaDTO> salas, long eventId);

    Completable salvarEventoParticipante(EventoParticipante eventoParticipante);

    Observable<List<Palestra>> getPalestrasPorEventoPorId(long eventoId);

    Observable<List<Palestra>> getPalestrasPorPalestrante(String nomePalestrante);

    Completable salvarPalestra(Palestra palestra);

    Observable<List<Participante>> getParticipantesPorEventoId(long eventoId);

    Completable salvarParticipante(long eventoId, Participante participante);

    Observable<List<Sala>> getSalasDisponiveisPorHorario(Date horarioPalestra);

    Observable<List<Sala>> getSalas();

    Maybe<List<Sala>> getSalasPorEvento(long eventoId);

    Maybe<List<Sala>> getSalasDisponiveisPorEvento(long eventoId);

    Single<Date> getEventoDatas();

    Observable<List<Sala>> getSalasDisponiveis(Date startTime, Date endTime);

    Observable<List<Palestra>> getPalestrasPorTitulo(String nome);
}
