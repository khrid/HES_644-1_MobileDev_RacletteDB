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

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<CheeseEntity> observableCheese;

    public CheeseViewModel(@NonNull Application application,
                            final Long cheeseId, CheeseRepository cheeseRepository) {
        super(application);

        this.application = application;

        repository = cheeseRepository;

        observableCheese = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        observableCheese.setValue(null);

        LiveData<CheeseEntity> cheese = repository.getCheese(cheeseId, application);

        // observe the changes of the account entity from the database and forward them
        observableCheese.addSource(cheese, observableCheese::setValue);
    }

    /**
     * A creator is used to inject the account id into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final Long cheeseId;

        private final CheeseRepository repository;

        public Factory(@NonNull Application application, Long cheeseId) {
            this.application = application;
            this.cheeseId = cheeseId;
            repository = ((BaseApp) application).getCheeseRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ch.hevs.students.raclettedb.viewmodel.cheese.CheeseViewModel(application, cheeseId, repository);
        }
    }

    /**
     * Expose the LiveData AccountEntity query so the UI can observe it.
     */
    public LiveData<CheeseEntity> getCheese() {
        return observableCheese;
    }

    public void createCheese(CheeseEntity cheese, OnAsyncEventListener callback) {
        repository.insert(cheese, callback, application);
    }

    public void updateCheese(CheeseEntity cheese, OnAsyncEventListener callback) {
        repository.update(cheese, callback, application);
    }
}