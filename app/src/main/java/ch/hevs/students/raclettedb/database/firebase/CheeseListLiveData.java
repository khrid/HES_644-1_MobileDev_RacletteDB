package ch.hevs.students.raclettedb.database.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;

public class CheeseListLiveData extends LiveData<List<CheeseEntity>> {
    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-CheeseListLiveData";

    private final DatabaseReference reference;
    private final MyValueEventListener listener = new MyValueEventListener();

    public CheeseListLiveData(DatabaseReference ref) {
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
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            setValue(toCheeses(snapshot));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "Can't listen to query " + reference, error.toException());
        }
    }

    private List<CheeseEntity> toCheeses(DataSnapshot dataSnapshot) {
        List<CheeseEntity> shielings = new ArrayList<>();
        for (DataSnapshot shielingSnapshot :
                dataSnapshot.getChildren()) {
            for (DataSnapshot cheeseSnapshot : shielingSnapshot.getChildren()) {
                CheeseEntity cheese = cheeseSnapshot.getValue(CheeseEntity.class);
                cheese.setId(cheeseSnapshot.getKey());
                cheese.setShieling(shielingSnapshot.getKey());
                shielings.add(cheese);
            }
        }
        return shielings;
    }
}
