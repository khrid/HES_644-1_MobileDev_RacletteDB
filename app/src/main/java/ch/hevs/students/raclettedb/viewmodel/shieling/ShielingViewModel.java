package ch.hevs.students.raclettedb.viewmodel.shieling;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.database.repository.ShielingRepository;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class ShielingViewModel  extends AndroidViewModel {

    private Application application;

    private ShielingRepository repository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<ShielingEntity> observableShieling;

    public ShielingViewModel(@NonNull Application application,
                           final Long shielingId, ShielingRepository shielingRepository) {
        super(application);

        this.application = application;

        repository = shielingRepository;

        observableShieling = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        observableShieling.setValue(null);

        LiveData<ShielingEntity> shieling = repository.getShieling(shielingId, application);

        // observe the changes of the account entity from the database and forward them
        observableShieling.addSource(shieling, observableShieling::setValue);
    }

    /**
     * A creator is used to inject the account id into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final Long shielingId;

        private final ShielingRepository repository;

        public Factory(@NonNull Application application, Long shielingId) {
            this.application = application;
            this.shielingId = shielingId;
            repository = ((BaseApp) application).getShielingRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel(application, shielingId, repository);
        }
    }

    /**
     * Expose the LiveData AccountEntity query so the UI can observe it.
     */
    public LiveData<ShielingEntity> getShieling() {
        return observableShieling;
    }

    public void createShieling(ShielingEntity shieling, OnAsyncEventListener callback) {
        repository.insert(shieling, callback, application);
    }

    public void updateShieling(ShielingEntity shieling, OnAsyncEventListener callback) {
        repository.update(shieling, callback, application);
    }
}