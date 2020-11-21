package ch.hevs.students.raclettedb.viewmodel.cheese;

import android.app.Application;

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

    private Application application;

    private CheeseRepository repository;

    private final MediatorLiveData<CheeseEntity> observableCheese;

    public CheeseViewModel(@NonNull Application application,
                            final String cheeseId, CheeseRepository cheeseRepository) {
        super(application);

        this.application = application;

        repository = cheeseRepository;

        observableCheese = new MediatorLiveData<>();
        observableCheese.setValue(null);

        LiveData<CheeseEntity> cheese = repository.getCheese(cheeseId);

        observableCheese.addSource(cheese, observableCheese::setValue);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final String cheeseId;

        private final CheeseRepository repository;

        public Factory(@NonNull Application application, String cheeseId) {
            this.application = application;
            this.cheeseId = cheeseId;
            //repository = ((BaseApp) application).getRoomCheeseRepository();
            repository = ((BaseApp) application).getCheeseRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new CheeseViewModel(application, cheeseId, repository);
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