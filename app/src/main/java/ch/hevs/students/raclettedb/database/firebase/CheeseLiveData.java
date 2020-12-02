package ch.hevs.students.raclettedb.database.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;

public class CheeseLiveData extends LiveData<CheeseEntity> {
    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-CheeseLiveData";

    private final DatabaseReference reference;
    private final CheeseLiveData.MyValueListener listener = new CheeseLiveData.MyValueListener();

    public CheeseLiveData(DatabaseReference ref) {
        this.reference = ref;
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive");
        reference.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive");
        super.onInactive();
    }

    private class MyValueListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                CheeseEntity entity = snapshot.getValue(CheeseEntity.class);
                entity.setId(snapshot.getKey());
                setValue(entity);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "Cant listen to query " + reference, error.toException());
        }
    }
}
