package ch.hevs.students.raclettedb;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import ch.hevs.students.raclettedb.database.AppDatabase;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.database.repository.ShielingRepository;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BaseApp extends Application {

    public static final String APP_NAME = "RacletteDB";

    public static final String ADMIN_PASSWORD = "BestCheeses";

    public static final LatLng NO_LOCATION = new LatLng(46.2878787,7.5330482);

    public static final boolean CLOUD_ACTIVE = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public CheeseRepository getCheeseRepository() { return CheeseRepository.getInstance(); }

    public ShielingRepository getShielingRepository() { return ShielingRepository.getInstance(); }
}