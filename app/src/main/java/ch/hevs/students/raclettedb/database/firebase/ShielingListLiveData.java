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
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;

public class ShielingListLiveData extends LiveData<List<ShielingEntity>> {
    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-ShielingListLiveData";

    private final DatabaseReference reference;
    private final MyValueEventListener listener = new MyValueEventListener();

    public ShielingListLiveData(DatabaseReference ref) {
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
            setValue(toShielings(snapshot));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, "Can't listen to query " + reference, error.toException());
        }
    }

    private List<ShielingEntity> toShielings(DataSnapshot dataSnapshot) {
        List<ShielingEntity> shielings = new ArrayList<>();
        for (DataSnapshot childSnapshot :
                dataSnapshot.getChildren()) {
            ShielingEntity shieling = childSnapshot.getValue(ShielingEntity.class);
            shieling.setId(childSnapshot.getKey());
            shielings.add(shieling);
        }
        return shielings;
    }
}
