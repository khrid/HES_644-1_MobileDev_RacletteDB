package ch.hevs.students.raclettedb.database.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.firebase.CheeseListLiveData;
import ch.hevs.students.raclettedb.database.firebase.CheeseLiveData;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class CheeseRepository {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-CheeseRepository";
    private static CheeseRepository instance;

    private CheeseRepository() {

    }

    public static CheeseRepository getInstance() {
        if (instance == null) {
            synchronized (CheeseRepository.class) {
                if (instance == null) {
                    instance = new CheeseRepository();
                }
            }
        }
        return instance;
    }

    public LiveData<CheeseEntity> getCheese(final String cheeseId, final String shielingId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("cheeses")
                .child(shielingId)
                .child(cheeseId);
        return new CheeseLiveData(reference);
    }

    public void insert(final CheeseEntity cheese, final OnAsyncEventListener callback) {
        // cheeses / <shielingName> / <key>
        Log.d(TAG, cheese+"");
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("cheeses");
        String key = reference.push().getKey();
        FirebaseDatabase.getInstance()
                .getReference("cheeses")
                .child(cheese.getShieling())
                .child(key)
                .setValue(cheese, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();
                    }
                });
    }

    public void update(final CheeseEntity cheese, final OnAsyncEventListener callback) {
        FirebaseDatabase.getInstance()
                .getReference("cheeses")
                .child(cheese.getShieling())
                .child(cheese.getId())
                .updateChildren(cheese.toMap(), (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();
                    }
                });

        // if shieling has changed
        if(!cheese.getShieling().equals(cheese.getOldShieling())) {
            FirebaseDatabase.getInstance()
                    .getReference("cheeses")
                    .child(cheese.getOldShieling())
                    .child(cheese.getId())
                    .removeValue((databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            callback.onFailure(databaseError.toException());
                        } else {
                            callback.onSuccess();
                        }
                    });
        }
    }

    public void delete(final CheeseEntity cheese, final OnAsyncEventListener callback) {
        FirebaseDatabase.getInstance()
                .getReference("cheeses")
                .child(cheese.getShieling())
                .child(cheese.getId())
                .removeValue((databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();
                    }
                });
    }

    public LiveData<List<CheeseEntity>> getAllCheeses() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("cheeses");
        return new CheeseListLiveData(reference);
    }

}
