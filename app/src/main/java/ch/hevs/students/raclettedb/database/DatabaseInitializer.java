package ch.hevs.students.raclettedb.database;

import android.os.AsyncTask;
import android.util.Log;

import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;

/**
 * Generates dummy data
 */
public class DatabaseInitializer {

    public static final String TAG = "DatabaseInitializer";

    public static void populateDatabase(final AppDatabase db) {
        Log.i(TAG, "Inserting demo data.");
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }


    private static void addCheese(final AppDatabase db, final String name) {
        CheeseEntity cheese = new CheeseEntity(name);
        db.cheeseDao().insert(cheese);
    }

    private static void addShieling(final AppDatabase db, final String name) {
        ShielingEntity shieling = new ShielingEntity(name);
        db.shielingDao().insert(shieling);
    }

    private static void populateWithTestData(AppDatabase db) {

        addShieling(db,"Alpage 1");
        addShieling(db,"Alpage 2");
        addShieling(db,"Alpage 3");
        addShieling(db,"Alpage 4");


        try {
            // Let's ensure that the clients are already stored in the database before we continue.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addCheese(db,"Anniviers");
        addCheese(db,"Bagnes");
        addCheese(db,"Tourtemagne");
        addCheese(db,"Martigny");
        addCheese(db,"Brigue");
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase database;

        PopulateDbAsync(AppDatabase db) {
            database = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(database);
            return null;
        }

    }
}
