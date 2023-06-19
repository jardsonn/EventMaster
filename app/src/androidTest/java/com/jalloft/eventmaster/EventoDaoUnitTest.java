package com.jalloft.eventmaster;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jalloft.eventmaster.data.entity.Evento;
import com.jalloft.eventmaster.db.EventMasterDatabase;
import com.jalloft.eventmaster.db.dao.EventoDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import dagger.hilt.android.testing.HiltAndroidTest;
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
@HiltAndroidTest
public class EventoDaoUnitTest {

    private EventoDao eventoDao;
    private EventMasterDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, EventMasterDatabase.class)
                .allowMainThreadQueries()
                .build();
        eventoDao = db.getEventoDao();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @After
    public void closeDb() throws IOException {
        db.close();
        RxAndroidPlugins.reset();
        RxJavaPlugins.reset();
    }

    @Test
    public void insertEvent(){
        Evento evento = new Evento("Evnto teste", new Date(), "Onde o vento faz a curva", "Lorem....");
        eventoDao.insert(evento);
        Observable<List<Evento>> eventosObservable = eventoDao.getEventos();
        TestObserver<List<Evento>> testObserver = eventosObservable.test();

//        testObserver.assertComplete();
        testObserver.assertNoErrors();

        testObserver.assertValue(eventos -> eventos.size() == 1);
        testObserver.assertValue(eventos -> eventos.stream().findFirst().get().getNome().equals("Evnto teste"));
    }
}
