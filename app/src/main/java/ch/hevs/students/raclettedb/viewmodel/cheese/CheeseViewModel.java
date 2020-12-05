package ch.hevs.students.raclettedb.viewmodel.cheese;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class CheeseViewModel  extends AndroidViewModel {
    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-CheeseViewModel";

    private Application application;

    private CheeseRepository repository;

    private final MediatorLiveData<CheeseEntity> observableCheese;

    public CheeseViewModel(@NonNull Application application,
                            final String cheeseId, final String shielingId, CheeseRepository cheeseRepository) {
        super(application);

        Log.d(TAG, "Building ViewModel with cheese id " + cheeseId);

        this.application = application;

        repository = cheeseRepository;

        observableCheese = new MediatorLiveData<>();
        observableCheese.setValue(null);

        if (cheeseId != null) {
            LiveData<CheeseEntity> cheese = repository.getCheese(cheeseId, shielingId);

            observableCheese.addSource(cheese, observableCheese::setValue);
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final String cheeseId;

        private final String shielingId;

        private final CheeseRepository repository;

        public Factory(@NonNull Application application, String cheeseId, String shielingId) {
            this.application = application;
            this.cheeseId = cheeseId;
            this.shielingId = shielingId;
            //repository = ((BaseApp) application).getRoomCheeseRepository();
            repository = ((BaseApp) application).getCheeseRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new CheeseViewModel(application, cheeseId, shielingId, repository);
        }
    }

    public LiveData<CheeseEntity> getCheese() {
        return observableCheese;
    }

    public void createCheese(CheeseEntity cheese, OnAsyncEventListener callback) {
        ((BaseApp) getApplication()).getCheeseRepository()
                .insert(cheese, callback);
    }

    public void updateCheese(CheeseEntity cheese, OnAsyncEventListener callback) {
        ((BaseApp) getApplication()).getCheeseRepository()
                .update(cheese, callback);
    }
}