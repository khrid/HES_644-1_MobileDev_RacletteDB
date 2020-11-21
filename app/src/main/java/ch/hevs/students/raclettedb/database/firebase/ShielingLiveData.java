package ch.hevs.students.raclettedb.database.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;

public class ShielingLiveData extends LiveData<ShielingEntity> {
    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-ShielingLiveData";

    private final DatabaseReference reference;
    private final ShielingLiveData.MyValueListener listener = new ShielingLiveData.MyValueListener();

    public ShielingLiveData(DatabaseReference ref) {
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
            ShielingEntity entity = snapshot.getValue(ShielingEntity.class);
            entity.setId(snapshot.getKey());
            setValue(entity);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "Cant listen to query " + reference, error.toException());
        }
    }
}
