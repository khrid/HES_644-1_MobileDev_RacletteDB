package ch.hevs.students.raclettedb.database;

import android.os.AsyncTask;
import android.util.Log;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;

/**
 * Generates dummy data
 */
public class DatabaseInitializer {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-"+"DatabaseInitializer";

    public static void populateDatabase(final AppDatabase db) {
        Log.i(TAG, "Inserting demo data.");
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }


    /*private static void addCheese(final AppDatabase db, final String name, final Long shieling) {
        CheeseEntity cheese = new CheeseEntity(name, shieling);
        db.cheeseDao().insert(cheese);
    }*/

    private static void addCheese(final AppDatabase db, final String name, final Long shieling, final String description, final String type) {
        CheeseEntity cheese = new CheeseEntity(name, shieling);
        cheese.setDescription(description);
        cheese.setType(type);
        db.cheeseDao().insert(cheese);
    }

    private static long addShieling(final AppDatabase db, final String name) {
        ShielingEntity shieling = new ShielingEntity(name);
        return db.shielingDao().insert(shieling);
    }

    private static long addShieling(final AppDatabase db, final String name, final String description, final float latitude, final float longitude) {
        ShielingEntity shieling = new ShielingEntity(name);
        shieling.setDescription(description);
        shieling.setLatitude(latitude);
        shieling.setLongitude(longitude);
        return db.shielingDao().insert(shieling);
    }

    private static void populateWithTestData(AppDatabase db) {

        /*long anniviers = addShieling(db,"Val d'Anniviers", "", 46.221237f, 7.578975f);
        long bagnes = addShieling(db,"Val de Bagnes", "",46.070715f, 7.223700f);*/
        long turtmann = addShieling(db,"Turtmanntal", "Alpage de Tourtemagne",46.251392f, 7.716753f);
        long champsec = addShieling(db, "Champsec", "Alpage de Champsec", 46.058210f, 7.241118f);
        long liddes = addShieling(db, "Liddes", "Alpage de Liddes", 45.986689f, 7.178619f);
        long lourtier = addShieling(db, "Lourtier", "Alpage de Lourtier", 46.055148f, 7.260309f);
        long orsiere = addShieling(db, "Orsières", "Alpage d'Orsières", 45.999245f, 7.106959f);
        long verbier = addShieling(db, "Verbier", "Alpage de Verbier", 46.100125f, 7.227464f);
        long volleges = addShieling(db, "Vollèges", "Alpage de Vollèges", 46.095292f, 7.148003f);


        // To be sure that the previous data are in the database
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*addCheese(db,"Vissoie", anniviers, "", "");
        addCheese(db,"Bagnes 4", bagnes, "", "");
        addCheese(db,"Bagnes 7", bagnes, "", "");
        addCheese(db,"Turtmann 1", turtmann, "", "");*/
        addCheese(db,"Wallis 65", turtmann, "Raclette Tourtemagne", "lait cru");
        addCheese(db, "Bagnes 25", champsec, "Raclette Champsec", "lait cru");
        addCheese(db, "Bagnes 4", liddes, "Raclette Liddes", "lait cru");
        addCheese(db, "Bagnes 30", lourtier, "Raclette Lourtier", "lait cru");
        addCheese(db, "Orsières", orsiere, "Raclette Orsières", "lait cru");
        addCheese(db, "Bagnes 1", verbier, "Raclette Verbier", "lait cru");
        addCheese(db, "Bagnes 98", volleges, "Raclette Vollèges", "lait cru");
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
