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


    private static void addCheese(final AppDatabase db, final String name, final Long shieling) {
        CheeseEntity cheese = new CheeseEntity(name, shieling);
        db.cheeseDao().insert(cheese);
    }

    private static long addShieling(final AppDatabase db, final String name) {
        ShielingEntity shieling = new ShielingEntity(name);
        return db.shielingDao().insert(shieling);
    }

    private static void populateWithTestData(AppDatabase db) {

        long anniviers = addShieling(db,"Val d'Anniviers");
        long bagnes = addShieling(db,"Val de Bagnes");
        long turtmann = addShieling(db,"Turtmanntal");


        // To be sure that the previous data are in the database
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addCheese(db,"Vissoie", anniviers);
        addCheese(db,"Bagnes 4", bagnes);
        addCheese(db,"Bagnes 7", bagnes);
        addCheese(db,"Turtmann 1", turtmann);
        addCheese(db,"Turtmann 3", turtmann);
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
