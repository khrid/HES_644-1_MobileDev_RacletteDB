package ch.hevs.students.raclettedb.database.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.database.firebase.ShielingListLiveData;
import ch.hevs.students.raclettedb.database.firebase.ShielingLiveData;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class ShielingRepository {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-ShielingRepository";
    private static ShielingRepository instance;

    private ShielingRepository() {

    }

    public static ShielingRepository getInstance() {
        if (instance == null) {
            synchronized (ShielingRepository.class) {
                if (instance == null) {
                    instance = new ShielingRepository();
                }
            }
        }
        return instance;
    }

    public ShielingLiveData getShieling(final String shielingId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("shielings").child(shielingId);
        return new ShielingLiveData(reference);
    }

    public void insert(final ShielingEntity shieling, final OnAsyncEventListener callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("shielings");
        //String key = reference.push().getKey();
        FirebaseDatabase.getInstance()
                .getReference("shielings")
                .child(shieling.getName())
                .setValue(shieling, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();
                    }
                });
    }

    public void update(final ShielingEntity shieling, final OnAsyncEventListener callback) {
        FirebaseDatabase.getInstance()
                .getReference("shielings")
                .child(shieling.getId())
                .updateChildren(shieling.toMap(), (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();
                    }
                });
    }

    public void delete(final ShielingEntity shieling, final OnAsyncEventListener callback) {
        FirebaseDatabase.getInstance()
                .getReference("shielings")
                .child(shieling.getId())
                .removeValue((databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();
                    }
                });
    }

    public LiveData<List<ShielingEntity>> getAllShielings() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("shielings");
        return new ShielingListLiveData(reference);
    }
}
