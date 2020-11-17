package ch.hevs.students.raclettedb.database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Executors;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.dao.CheeseDao;
import ch.hevs.students.raclettedb.database.dao.ShielingDao;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;

@Database(entities = {ShielingEntity.class,CheeseEntity.class}, version = 12)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-"+"AppDatabase";

    private static AppDatabase instance;

    private static final String DATABASE_NAME = "raclettedb-database";

    public abstract CheeseDao cheeseDao();
    public abstract ShielingDao shielingDao();

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = buildDatabase(context.getApplicationContext());
                    instance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext) {
        Log.i(TAG, "Database will be initialized.");
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase database = AppDatabase.getInstance(appContext);
                            initializeDemoData(database);
                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });

                    }
                })
                // A faire quand on met à jour la structure de la DB (avec incrémentation du numéro de version de la DB)
                .fallbackToDestructiveMigration()
                .build();
    }

    public static void initializeDemoData(final AppDatabase database) {
        Executors.newSingleThreadExecutor().execute(() -> {
            database.runInTransaction(() -> {
                Log.i(TAG, "Wipe database.");
                database.cheeseDao().deleteAll();
                database.shielingDao().deleteAll();

                DatabaseInitializer.populateDatabase(database);
            });
        });
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            Log.i(TAG, "Database initialized.");
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
