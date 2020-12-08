package ch.hevs.students.raclettedb.database.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
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


        FirebaseDatabase.getInstance()
                .getReference("shielings")
                .child(shieling.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if the shieling name is unique (case sensitive !)
                if(!snapshot.exists()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("shielings");
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
                } else {
                    Log.d(TAG, "Shieling with same name already exists");
                    callback.onFailure(new Exception("Shieling with same name already exists"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void update(final ShielingEntity shieling, final OnAsyncEventListener callback) {

        // has the name changed ?
        // it has not
        if ( shieling.getName().equals(shieling.getOldName())) {
            // we are updating
            FirebaseDatabase.getInstance()
                    .getReference("shielings")
                    .child(shieling.getName())
                    .updateChildren(shieling.toMap(), (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            callback.onFailure(databaseError.toException());
                        } else {
                            callback.onSuccess();
                        }
                    });
        } else {
            // it has

            // is the new name already used ?
            FirebaseDatabase.getInstance()
                    .getReference("shielings")
                    .child(shieling.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // it is not, we are updating
                    if (!snapshot.exists()) {
                        FirebaseDatabase.getInstance()
                                .getReference("shielings")
                                .child(shieling.getOldName())
                                .removeValue((databaseError, databaseReference) -> {
                                    if (databaseError != null) {
                                        callback.onFailure(databaseError.toException());
                                    } else {
                                        callback.onSuccess();
                                    }
                                });

                        FirebaseDatabase.getInstance()
                                .getReference("shielings")
                                .child(shieling.getName())
                                .updateChildren(shieling.toMap(), (databaseError, databaseReference) -> {
                                    if (databaseError != null) {
                                        callback.onFailure(databaseError.toException());
                                    } else {
                                        callback.onSuccess();
                                    }
                                });

                        // we have to move the cheeses in the new branch
                        DatabaseReference oldTree = FirebaseDatabase.getInstance()
                                .getReference("cheeses")
                                .child(shieling.getOldName());

                        oldTree.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Log.d(TAG, snapshot.getChildrenCount() + "");
                                    for (DataSnapshot childSnapshot :
                                            snapshot.getChildren()) {
                                        CheeseEntity entity = childSnapshot.getValue(CheeseEntity.class);
                                        entity.setId(childSnapshot.getKey());
                                        entity.setShieling(shieling.getName());

                                        FirebaseDatabase.getInstance()
                                                .getReference("cheeses")
                                                .child(shieling.getName())
                                                .child(entity.getId())
                                                .setValue(entity);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, error.toException().toString());
                            }
                        });

                        FirebaseDatabase.getInstance()
                                .getReference("cheeses")
                                .child(shieling.getOldName())
                                .removeValue();
                    } else {
                        // the new name is already used
                        callback.onFailure(new Exception("Shieling with same name already exists"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        if (!shieling.getName().equals(shieling.getOldName())) {

        } else {

        }
    }

    public void delete(final ShielingEntity shieling, final OnAsyncEventListener callback) {
        FirebaseDatabase.getInstance()
                .getReference("shielings")
                .child(shieling.getName())
                .removeValue((databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        callback.onFailure(databaseError.toException());
                    } else {
                        callback.onSuccess();

                        // Delete all cheeses for this shieling
                        FirebaseDatabase.getInstance()
                                .getReference("cheeses")
                                .child(shieling.getName())
                                .removeValue((databaseErrorCheese, databaseReferenceCheese) -> {
                                    if (databaseErrorCheese != null) {
                                        callback.onFailure(databaseErrorCheese.toException());
                                    } else {
                                        callback.onSuccess();
                                    }
                                });
                    }
                });
    }

    public LiveData<List<ShielingEntity>> getAllShielings() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("shielings");
        return new ShielingListLiveData(reference);
    }
}
