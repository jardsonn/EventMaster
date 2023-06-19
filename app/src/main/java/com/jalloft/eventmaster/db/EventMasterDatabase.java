package com.jalloft.eventmaster.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.jalloft.eventmaster.converters.DateConverter;
import com.jalloft.eventmaster.data.entity.Evento;
import com.jalloft.eventmaster.data.entity.EventoParticipante;
import com.jalloft.eventmaster.data.entity.Palestra;
import com.jalloft.eventmaster.data.entity.Participante;
import com.jalloft.eventmaster.data.entity.Sala;
import com.jalloft.eventmaster.db.dao.EventoDao;
import com.jalloft.eventmaster.db.dao.EventoParticipanteDao;
import com.jalloft.eventmaster.db.dao.PalestraDao;
import com.jalloft.eventmaster.db.dao.ParticipanteDao;
import com.jalloft.eventmaster.db.dao.SalaDao;

@Database(entities = {Evento.class, Palestra.class, Participante.class, Sala.class, EventoParticipante.class}, version = 1)
@TypeConverters(DateConverter.
        class)
public abstract class EventMasterDatabase extends RoomDatabase {

    private static final Object lock = new Object();

    public abstract EventoDao getEventoDao();

    public abstract EventoParticipanteDao getEventoParticipanteDao();

    public abstract PalestraDao getPalestraDao();

    public abstract SalaDao getSalaDao();

    public abstract ParticipanteDao getParticipanteDao();

    private static volatile EventMasterDatabase INSTANCE;

    public static EventMasterDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, EventMasterDatabase.class, "event_master.db")
                            .fallbackToDestructiveMigration()
//                            .addCallback(new EventMasterDatabaseCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

//    private static class EventMasterDatabaseCallback extends Callback {
//        @Override
//        public void onOpen(@NonNull SupportSQLiteDatabase db) {
//            super.onOpen(db);
//        }
//    }


}
