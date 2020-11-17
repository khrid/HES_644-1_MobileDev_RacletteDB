package ch.hevs.students.raclettedb.viewmodel.shieling;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.database.repository.ShielingRepository;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class ShielingListViewModel extends AndroidViewModel {

    private Application application;

    private ShielingRepository repository;

    private final MediatorLiveData<List<ShielingEntity>> observableShielings;

    public ShielingListViewModel(@NonNull Application application,
                               ShielingRepository shielingRepository) {
        super(application);

        this.application = application;

        repository = shielingRepository;

        observableShielings = new MediatorLiveData<>();
        observableShielings.setValue(null);

        LiveData<List<ShielingEntity>> shielings = repository.getAllShielings(application);

        observableShielings.addSource(shielings, observableShielings::setValue);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final ShielingRepository shielingRepository;

        public Factory(@NonNull Application application) {
            this.application = application;
            shielingRepository = ((BaseApp) application).getShielingRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new ch.hevs.students.raclettedb.viewmodel.shieling.ShielingListViewModel(application,shielingRepository);
        }
    }

    public LiveData<List<ShielingEntity>> getShielings() {
        return observableShielings;
    }

    public void deleteShieling(ShielingEntity shieling, OnAsyncEventListener callback) {
        repository.delete(shieling, callback, application);
    }

}
