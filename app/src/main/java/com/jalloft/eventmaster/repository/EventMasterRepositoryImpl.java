package com.jalloft.eventmaster.repository;

import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.data.entity.Evento;
import com.jalloft.eventmaster.data.entity.EventoParticipante;
import com.jalloft.eventmaster.data.entity.Palestra;
import com.jalloft.eventmaster.data.entity.Participante;
import com.jalloft.eventmaster.data.entity.Sala;
import com.jalloft.eventmaster.data.dto.ParticipanteEventoDTO;
import com.jalloft.eventmaster.db.dao.EventoDao;
import com.jalloft.eventmaster.db.dao.EventoParticipanteDao;
import com.jalloft.eventmaster.db.dao.PalestraDao;
import com.jalloft.eventmaster.db.dao.ParticipanteDao;
import com.jalloft.eventmaster.db.dao.SalaDao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventMasterRepositoryImpl implements EventMasterRepository {

    private final EventoDao eventoDao;
    private final EventoParticipanteDao eventoParticipanteDao;
    private final PalestraDao palestraDao;
    private final ParticipanteDao participanteDao;
    private final SalaDao salaDao;

    @Inject
    public EventMasterRepositoryImpl(EventoDao eventoDao, EventoParticipanteDao eventoParticipanteDao, PalestraDao palestraDao, ParticipanteDao participanteDao, SalaDao salaDao) {
        this.eventoDao = eventoDao;
        this.eventoParticipanteDao = eventoParticipanteDao;
        this.palestraDao = palestraDao;
        this.participanteDao = participanteDao;
        this.salaDao = salaDao;
    }

    @Override
    public Observable<List<Evento>> getEventos() {
        return eventoDao.getEventos().subscribeOn(Schedulers.io());
    }

    @Override
    public Single<String> getEventoNome(long id) {
        return eventoDao.getEventoNome(id).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Palestra>> getPalestras() {
        return palestraDao.getPalestras().subscribeOn(Schedulers.io());
    }

    @Override
    public Completable salvarEvento(EventoDTO evento) {
        return Completable.fromAction(() -> {
            long eventId = eventoDao.insert(evento.toEvento());
            var salas = evento.getSalas().stream().map(salaDTO -> salaDTO.toSala(eventId)).collect(Collectors.toList());
            salaDao.insertAll(salas);
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable salvarSalas(Collection<SalaDTO> salas, long eventId) {
        return Completable.fromAction(() -> salaDao.insertAll(salas.stream().map(salaDTO -> salaDTO.toSala(eventId)).collect(Collectors.toList()))).subscribeOn(Schedulers.io());
    }

    //    @Override
//    public Completable salvarSalas(Collection<SalaDTO> salas) {
//        return Completable.fromAction(() -> salaDao.insertAll(salas)).subscribeOn(Schedulers.io());
//    }

    @Override
    public Completable salvarEventoParticipante(EventoParticipante eventoParticipante) {
        return Completable.fromAction(() -> eventoParticipanteDao.insert(eventoParticipante)).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable salvarSala(Sala sala) {
        return Completable.fromAction(() -> salaDao.insert(sala)).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Palestra>> getPalestrasPorEventoPorId(long eventoId) {
        return palestraDao.getPalestrasPorEventoId(eventoId).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Palestra>> getPalestrasPorTitulo(String titulo) {
        return palestraDao.getPalestrasPorTitulo(titulo).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Palestra>> getPalestrasPorPalestrante(String nomePalestrante) {
        return palestraDao.getPalestrasPorPalestrante(nomePalestrante).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable salvarPalestra(Palestra palestra) {
        return Completable.fromAction(() -> palestraDao.insert(palestra)).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Participante>> getParticipantesPorEventoId(long eventoId) {
        return participanteDao.getParticipantesPorEventoId(eventoId).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable salvarParticipante(long eventoId, Participante participante) {
        return Completable.fromAction(() -> {
            var participanteId = participanteDao.insert(participante);
            eventoParticipanteDao.insert(new EventoParticipante(eventoId, participanteId));
        }).subscribeOn(Schedulers.io());
//        return Completable.fromAction(() -> participanteDao.insert(participante)).subscribeOn(Schedulers.io()).doOnComplete(() -> eventoParticipanteDao.insert(new EventoParticipante(participante.getEventoId(), participante.getParticipanteId())));
    }

    @Override
    public Observable<List<Sala>> getSalasDisponiveisPorHorario(Date horarioPalestra) {
        return salaDao.getSalasDisponiveisPorHorario(horarioPalestra).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Sala>> getSalas() {
        return salaDao.getSalas().subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<List<Sala>> getSalasPorEvento(long eventoId) {
        return salaDao.getSalaPorEvento(eventoId).subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<List<Sala>> getSalasDisponiveisPorEvento(long eventoId) {
        return salaDao.getSalasDisponiveisPorEvento(eventoId).subscribeOn(Schedulers.io());
    }


    @Override
    public Single<Date> getEventoDatas() {
        return eventoDao.getEventoDatas().subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Sala>> getSalasDisponiveis(Date startTime, Date endTime) {
        return salaDao.getSalasDisponiveis(startTime, endTime).subscribeOn(Schedulers.io());
    }
}
