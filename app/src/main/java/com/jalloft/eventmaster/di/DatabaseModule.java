package com.jalloft.eventmaster.di;

import android.content.Context;

import com.jalloft.eventmaster.db.EventMasterDatabase;
import com.jalloft.eventmaster.db.dao.EventoDao;
import com.jalloft.eventmaster.db.dao.EventoParticipanteDao;
import com.jalloft.eventmaster.db.dao.PalestraDao;
import com.jalloft.eventmaster.db.dao.ParticipanteDao;
import com.jalloft.eventmaster.db.dao.SalaDao;
import com.jalloft.eventmaster.repository.EventMasterRepository;
import com.jalloft.eventmaster.repository.EventMasterRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Singleton
    @Provides
    public EventMasterDatabase provideDataBase(@ApplicationContext Context context) {
        return EventMasterDatabase.getDatabase(context);
    }

    @Singleton
    @Provides
    public EventoDao provideEventoDao(EventMasterDatabase eventMasterDatabase) {
        return eventMasterDatabase.getEventoDao();
    }


    @Singleton
    @Provides
    public EventoParticipanteDao provideEventoParticipanteDao(EventMasterDatabase eventMasterDatabase) {
        return eventMasterDatabase.getEventoParticipanteDao();
    }


    @Singleton
    @Provides
    public PalestraDao providePalestraDao(EventMasterDatabase eventMasterDatabase) {
        return eventMasterDatabase.getPalestraDao();
    }


    @Singleton
    @Provides
    public ParticipanteDao provideParticipanteDao(EventMasterDatabase eventMasterDatabase) {
        return eventMasterDatabase.getParticipanteDao();
    }


    @Singleton
    @Provides
    public SalaDao provideSalaDao(EventMasterDatabase eventMasterDatabase) {
        return eventMasterDatabase.getSalaDao();
    }

    @Singleton
    @Provides
    public EventMasterRepository provideEventMasterRepository(EventoDao eventoDao, EventoParticipanteDao eventoParticipanteDao, PalestraDao palestraDao, ParticipanteDao participanteDao, SalaDao salaDao){
        return new EventMasterRepositoryImpl(eventoDao,  eventoParticipanteDao,  palestraDao, participanteDao, salaDao);
    }

}
