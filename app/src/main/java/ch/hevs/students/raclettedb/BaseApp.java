package ch.hevs.students.raclettedb;

import android.app.Application;

import ch.hevs.students.raclettedb.database.AppDatabase;
import ch.hevs.students.raclettedb.database.repository.AccountRepository;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.database.repository.ClientRepository;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BaseApp extends Application {

    public static final String ADMIN_PASSWORD = "castor";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public AccountRepository getAccountRepository() {
        return AccountRepository.getInstance();
    }

    public ClientRepository getClientRepository() {
        return ClientRepository.getInstance();
    }

    public CheeseRepository getCheeseRepository() { return CheeseRepository.getInstance(); }
}