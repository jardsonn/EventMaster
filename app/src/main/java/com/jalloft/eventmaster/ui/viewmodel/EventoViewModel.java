package com.jalloft.eventmaster.ui.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jalloft.eventmaster.R;
import com.jalloft.eventmaster.data.dto.EventoDTO;
import com.jalloft.eventmaster.data.dto.PalestraDTO;
import com.jalloft.eventmaster.data.dto.SalaDTO;
import com.jalloft.eventmaster.data.entity.Evento;
import com.jalloft.eventmaster.data.entity.EventoParticipante;
import com.jalloft.eventmaster.data.entity.Palestra;
import com.jalloft.eventmaster.data.entity.Participante;
import com.jalloft.eventmaster.data.entity.Sala;
import com.jalloft.eventmaster.data.dto.ParticipanteEventoDTO;
import com.jalloft.eventmaster.repository.EventMasterRepository;
import com.jalloft.eventmaster.repository.EventMasterRepositoryImpl;
import com.jalloft.eventmaster.ui.states.ResponseUiState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class EventoViewModel extends ViewModel {

    public static final int INDEX_ALL_LECTURE = -1;

    private final EventMasterRepository repo;
    public final MutableLiveData<Date> currentEditionDate = new MutableLiveData<>();
    public final MutableLiveData<List<Date>> listaDeDatas = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<SalaDTO>> currentEditionSalas = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<SalaDTO>> salasDisponiveis = new MutableLiveData<>(new ArrayList<>());
    public final MutableLiveData<List<EventoDTO>> events = new MutableLiveData<>();
    public final MutableLiveData<List<PalestraDTO>> lectures = new MutableLiveData<>();
//    public final MutableLiveData<List<ParticipanteEventoDTO>> palestrantes = new MutableLiveData<>();

    public final MutableLiveData<Boolean> existeSalaCadastrada = new MutableLiveData<>();
    public final MutableLiveData<Boolean> existeEventoCadastrada = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<Integer>> cadastrarEventoState = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<Integer>> cadastrarSalaState = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<Integer>> cadastrarPalestraState = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<List<EventoDTO>>> eventosListUiState = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<List<PalestraDTO>>> palestrasListUiState = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<List<SalaDTO>>> salasListUiState = new MutableLiveData<>();
    public MutableLiveData<ResponseUiState<List<ParticipanteEventoDTO>>> participantesListUiState = new MutableLiveData<>();

    public MutableLiveData<ResponseUiState<List<PalestraDTO>>> buscarPalestrasListUiState = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Inject
    public EventoViewModel(EventMasterRepository repo) {
        this.repo = repo;
        carregarListaEventos();
        carregarListaPalestras(INDEX_ALL_LECTURE);
        carregarListaSalas();
    }

    public Observable<List<Evento>> getEventos() {
        return repo.getEventos();
    }

    public void buscarPalestras(String query, SearchBy searchBy) {
        if (buscarPalestrasListUiState.getValue() == null) {
            buscarPalestrasListUiState.setValue(new ResponseUiState.Loading<>());
        }
        if (TextUtils.isEmpty(query)) {
            buscarPalestrasListUiState.setValue(new ResponseUiState.Success<>(new ArrayList<>()));
            return;
        }

        Observable<List<Palestra>> observable = searchBy == SearchBy.LECTURE_TITLE ? repo.getPalestrasPorTitulo(query) : repo.getPalestrasPorPalestrante(query);

        Disposable disposable = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(palestras -> {
                    var palestrasLista = palestras.stream().map(palestra -> {
                        PalestraDTO palestraDTO = palestra.palestraDTO();
                        String eventoName = repo.getEventoNome(palestra.getEventoId()).blockingGet();
                        palestraDTO.setEventoNome(eventoName);
                        return palestraDTO;
                    }).collect(Collectors.toList());
                    System.out.println("EventoViewModel.buscarPalestras sucesso = " + palestrasLista);
                    buscarPalestrasListUiState.setValue(new ResponseUiState.Success<>(palestrasLista));
                }, error -> {
                    System.out.println("EventoViewModel.buscarPalestras error = " + error.getMessage());
                    buscarPalestrasListUiState.setValue(new ResponseUiState.Failure<>(R.string.erro_carregar_palestras));
                });
        compositeDisposable.add(disposable);
    }

    public void carregarListaParticipantes(long eventId) {
        if (participantesListUiState.getValue() == null) {
            participantesListUiState.setValue(new ResponseUiState.Loading<>());
        }

        var disposable = repo.getParticipantesPorEventoId(eventId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(participantes -> {
                            var participanteList = participantes.stream().map(Participante::participanteEventoDTO).collect(Collectors.toList());
                            participantesListUiState.setValue(new ResponseUiState.Success<>(participanteList));
                            System.out.println("EventoViewModel.carregarListaParticipantes carregada com sucesso");
                        }, error -> {
                            participantesListUiState.setValue(new ResponseUiState.Failure<>(R.string.erro_carregar_participantes));
                            System.out.println("EventoViewModel.carregarListaParticipantes ocorreu o erro = " + error.getMessage());
                        }
                );
        compositeDisposable.add(disposable);
    }

    public void carregarListaSalas(Date startDate, Date endDate) {
        if (salasListUiState.getValue() == null) {
            salasListUiState.setValue(new ResponseUiState.Loading<>());
        }
        var disposable = repo.getSalasDisponiveis(startDate, endDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        salas -> {
                            var salasList = salas.stream().map(sala -> {
                                SalaDTO salaDTO = sala.toSalaDTO();
                                String eventoName = repo.getEventoNome(sala.getEventoId()).blockingGet();
                                salaDTO.setEventoName(eventoName);
                                return salaDTO;
                            }).collect(Collectors.toList());
                            salasListUiState.setValue(new ResponseUiState.Success<>(salasList));
                        }, error -> {
                            salasListUiState.setValue(new ResponseUiState.Failure<>(R.string.erro_carregar_salas));
                            System.out.println("EventoViewModel.carregarListaSalas " + error.getMessage());
                        }
                );
        compositeDisposable.add(disposable);
    }

    public void getSalasDisponiveisPorEvento(long eventoId) {
        var disposable = repo.getSalasDisponiveisPorEvento(eventoId).observeOn(AndroidSchedulers.mainThread())
                .map(salas -> salas.stream().map(Sala::toSalaDTO).collect(Collectors.toList()))
                .subscribe(salasDisponiveis::setValue,
                        error -> {
                            salasDisponiveis.setValue(new ArrayList<>());
                        }
                );
        compositeDisposable.add(disposable);

    }

    public void carregarListaSalas() {
        if (salasListUiState.getValue() == null) {
            salasListUiState.setValue(new ResponseUiState.Loading<>());
        }
        var completable = repo.getSalas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(salas -> {
                    var salasList = salas.stream().map(sala -> {
                        SalaDTO salaDTO = sala.toSalaDTO();
                        String eventoName = repo.getEventoNome(sala.getEventoId()).blockingGet();
                        salaDTO.setEventoName(eventoName);
                        return salaDTO;
                    }).collect(Collectors.toList());
                    existeSalaCadastrada.setValue(!salasList.isEmpty());
                    salasListUiState.setValue(new ResponseUiState.Success<>(salasList));
                    System.out.println("ViewModel lista de salas = " + salas);
                }, error -> {
                    salasListUiState.setValue(new ResponseUiState.Failure<>(R.string.erro_carregar_salas));
                    System.out.println("ViewModel erro ao obter lista: " + error.getMessage());
                });

        compositeDisposable.add(completable);

    }

    public void carregarListaPalestras(long eventId) {
        if (palestrasListUiState.getValue() == null) {
            palestrasListUiState.setValue(new ResponseUiState.Loading<>());
        }

        Observable<List<Palestra>> palestrasObservable = eventId == -1 ? repo.getPalestras() : repo.getPalestrasPorEventoPorId(eventId);

        var completable = palestrasObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(palestras -> {
                    var palestrasLista = palestras.stream().map(palestra -> {
                        PalestraDTO palestraDTO = palestra.palestraDTO();
                        String eventoName = repo.getEventoNome(palestra.getEventoId()).blockingGet();
                        System.out.println("EventoViewModel.carregarListaPalestras eventoName = " + eventoName);
                        System.out.println("EventoViewModel.carregarListaPalestras palestra.getId = " + palestra.getId());
                        palestraDTO.setEventoNome(eventoName);
                        return palestraDTO;
                    }).collect(Collectors.toList());
                    palestrasListUiState.setValue(new ResponseUiState.Success<>(palestrasLista));
                    System.out.println("ViewModel lista de eventos = " + palestras);
                }, error -> {
                    palestrasListUiState.setValue(new ResponseUiState.Failure<>(R.string.erro_carregar_palestras));
                    System.out.println("ViewModel erro ao obter lista: " + error.getMessage());
                });

        compositeDisposable.add(completable);

    }

    public void carregarListaEventos() {
        if (eventosListUiState.getValue() == null) {
            eventosListUiState.setValue(new ResponseUiState.Loading<>());
        }
        var completable = repo.getEventos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventos -> {
                    listaDeDatas.setValue(eventos.stream().map(Evento::getData).collect(Collectors.toList()));
                    existeEventoCadastrada.setValue(!eventos.isEmpty());
                    var eventosLista = eventos.stream().map(Evento::eventoDTO).collect(Collectors.toList());
                    events.setValue(eventosLista);
                    eventosListUiState.setValue(new ResponseUiState.Success<>(eventosLista));
                    System.out.println("ViewModel lista de eventos = " + eventos);
                }, error -> {
                    eventosListUiState.setValue(new ResponseUiState.Failure<>(R.string.erro_carregar_eventos));
                    System.out.println("ViewModel erro ao obter lista: " + error.getMessage());
                });

        compositeDisposable.add(completable);

    }


    public void salvarPalestra(PalestraDTO palestra) {
        cadastrarPalestraState.setValue(new ResponseUiState.Loading<>());
        if (TextUtils.isEmpty(palestra.getTitulo())) {
            cadastrarPalestraState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_palestra_titulo_vazio));
        } else if (TextUtils.isEmpty(palestra.getNomePalestrante())) {
            cadastrarPalestraState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_palestra_palestrante_vazio));
        } else if (palestra.getEventoId() == -1) {
            cadastrarPalestraState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_palestra_evento_nao_definido));
        } else if (palestra.getSalaId() == -1) {
            cadastrarPalestraState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_palestra_sala_nao_definida));
        } else if (palestra.getHorarioRealizacao() == null) {
            cadastrarPalestraState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_palestra_horario_definido));
        } else {
            var disposable = repo.salvarPalestra(palestra.toPalestra())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        cadastrarPalestraState.setValue(new ResponseUiState.Success<>(R.string.sucesso_ao_salvar_palestra));
                    }, error -> {
                        cadastrarPalestraState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_palestra_desconhecido));
                        System.out.println("EventoViewModel erro ao salvar palestra: " + error.getMessage());
                    });
            compositeDisposable.add(disposable);
        }

    }

    public void salvarParticipante(ParticipanteEventoDTO participante) {
        var disposable = repo.salvarParticipante(participante.getEventoId(), participante.toParticipante())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    System.out.println("EventoViewModel.salvarParticipante sucesso");
                }, error -> {
                    System.out.println("EventoViewModel.salvarParticipante error: " + error.getMessage());
                });
        compositeDisposable.add(disposable);
    }

    public Completable salvarEventoParticipante(EventoParticipante eventoParticipante) {
        return repo.salvarEventoParticipante(eventoParticipante);
    }

    public void salvarSala(SalaDTO sala) {
        cadastrarSalaState.setValue(new ResponseUiState.Loading<>());
        if (TextUtils.isEmpty(sala.getNome())) {
            cadastrarSalaState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_sala_sem_nome));
        } else if (sala.getEventoId() == -1) {
            cadastrarSalaState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_sala_selecione_evento));
        } else if (sala.getCapacidadeMaxima() <= 0) {
            cadastrarSalaState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_sala_sem_capacidade));
        }else {
            var disposable = repo.salvarSala(sala.toSala())
             .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        cadastrarSalaState.setValue(new ResponseUiState.Success<>(R.string.success_room_saved));
                        System.out.println("EventoViewModel.salvarSala salvo");
                    }, error -> {
                        cadastrarSalaState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_sala_desconhecido));
                        System.out.println("EventoViewModel erro ao salvar sala: " + error.getMessage());
                    });
            compositeDisposable.add(disposable);
        }

    }


    public void salvarEvento(EventoDTO evento) {
        cadastrarEventoState.setValue(new ResponseUiState.Loading<>());
        if (TextUtils.isEmpty(evento.getNome())) {
            cadastrarEventoState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_evento_nome_vazio));
        } else if (TextUtils.isEmpty(evento.getLocal())) {
            cadastrarEventoState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_evento_local_vazio));
        } else if (evento.getData() == null) {
            cadastrarEventoState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_evento_data_vazio));
        } else if (evento.getSalas() != null && evento.getSalas().isEmpty()) {
            cadastrarEventoState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_evento_sala_vazia));
        } else {
            var disposable = repo.salvarEvento(evento)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        cadastrarEventoState.setValue(new ResponseUiState.Success<>(R.string.success_event_saved));
                    }, error -> {
                        cadastrarEventoState.setValue(new ResponseUiState.Failure<>(R.string.erro_ao_salvar_evento_desconhecido));
                        System.out.println("EventoViewModel erro ao salvar evento: " + error.getMessage());
                    });
            compositeDisposable.add(disposable);
        }
    }

    public Observable<List<Palestra>> getPalestrasPorEventoPorId(int eventoId) {
        return repo.getPalestrasPorEventoPorId(eventoId);
    }


    public Observable<List<Participante>> getParticipantesPorEventoId(int eventoId) {
        return repo.getParticipantesPorEventoId(eventoId);
    }

    public Observable<List<Sala>> getSalasDisponiveisPorHorario(Date horarioPalestra) {
        return repo.getSalasDisponiveisPorHorario(horarioPalestra);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
