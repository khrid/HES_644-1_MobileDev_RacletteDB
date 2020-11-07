package ch.hevs.students.raclettedb.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.async.shieling.CreateShieling;
import ch.hevs.students.raclettedb.database.async.shieling.DeleteShieling;
import ch.hevs.students.raclettedb.database.async.shieling.UpdateShieling;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class ShielingRepository {

    private static ShielingRepository instance;

    private ShielingRepository() {
    }

    public static ShielingRepository getInstance() {
        if (instance == null) {
            instance = new ShielingRepository();
        }
        return instance;
    }

    public LiveData<ShielingEntity> getShieling(final Long shielingId, Application application) {
        return ((BaseApp) application).getDatabase().shielingDao().getById(shielingId);
    }


    public LiveData<List<ShielingEntity>> getAllShielings(Application application) {
        return ((BaseApp) application).getDatabase().shielingDao().getAll();
    }

    public void insert(final ShielingEntity shieling, OnAsyncEventListener callback,
                       Application application) {
        new CreateShieling(application, callback).execute(shieling);
    }

    public void update(final ShielingEntity shieling, OnAsyncEventListener callback,
                       Application application) {
        new UpdateShieling(application, callback).execute(shieling);
    }

    public void delete(final ShielingEntity shieling, OnAsyncEventListener callback,
                       Application application) {
        new DeleteShieling(application, callback).execute(shieling);
    }

}
