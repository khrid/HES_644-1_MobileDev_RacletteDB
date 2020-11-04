package ch.hevs.students.raclettedb.viewmodel.cheese;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class CheeseListViewModel extends AndroidViewModel {

    private Application application;

    private CheeseRepository repository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<CheeseEntity>> observableCheeses;

    public CheeseListViewModel(@NonNull Application application,
                                CheeseRepository cheeseRepository) {
        super(application);

        this.application = application;

        repository = cheeseRepository;

        observableCheeses = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        observableCheeses.setValue(null);

        LiveData<List<CheeseEntity>> cheeses = repository.getAllCheeses(application);

        // observe the changes of the entities from the database and forward them
        observableCheeses.addSource(cheeses, observableCheeses::setValue);
    }

    /**
     * A creator is used to inject the account id into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final CheeseRepository cheeseRepository;

        public Factory(@NonNull Application application) {
            this.application = application;
            cheeseRepository = ((BaseApp) application).getCheeseRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ch.hevs.students.raclettedb.viewmodel.cheese.CheeseListViewModel(application,cheeseRepository);
        }
    }

    /**
     * Expose the LiveData AccountEntities query so the UI can observe it.
     */
    public LiveData<List<CheeseEntity>> getCheeses() {
        return observableCheeses;
    }

    public void deleteCheese(CheeseEntity cheese, OnAsyncEventListener callback) {
        repository.delete(cheese, callback, application);
    }

}
