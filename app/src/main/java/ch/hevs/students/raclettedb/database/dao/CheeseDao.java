package ch.hevs.students.raclettedb.database.dao;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.hevs.students.raclettedb.database.entity.CheeseEntity;

@Dao
public interface CheeseDao {


    @Query("SELECT * FROM cheese WHERE id = :id")
    LiveData<CheeseEntity> getById(Long id);

    @Query("SELECT * from cheese ORDER BY name ASC")
    LiveData<List<CheeseEntity>> getAll();

    @Insert()
    long insert(CheeseEntity cheese) throws SQLiteConstraintException;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CheeseEntity> cheeses);

    @Update
    void update(CheeseEntity cheese);

    @Delete
    void delete(CheeseEntity cheese);

    @Query("DELETE FROM cheese")
    void deleteAll();


}
