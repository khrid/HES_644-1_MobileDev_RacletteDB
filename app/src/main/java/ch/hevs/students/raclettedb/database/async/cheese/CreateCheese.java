package ch.hevs.students.raclettedb.database.async.cheese;

import android.app.Application;
import android.os.AsyncTask;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class CreateCheese extends AsyncTask<CheeseEntity, Void, Void> {

    private Application application;
    private OnAsyncEventListener callback;
    private Exception exception;

    public CreateCheese(Application application, OnAsyncEventListener callback) {
        this.application = application;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(CheeseEntity... params) {
        try {
            for (CheeseEntity cheese : params)
                ((BaseApp) application).getDatabase().cheeseDao().insert(cheese);
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callback != null) {
            if (exception == null) {
                callback.onSuccess();
            } else {
                callback.onFailure(exception);
            }
        }
    }
}

