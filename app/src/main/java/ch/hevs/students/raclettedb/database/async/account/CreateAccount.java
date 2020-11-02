package ch.hevs.students.raclettedb.database.async.account;

import android.app.Application;
import android.os.AsyncTask;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.database.entity.AccountEntity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;

public class CreateAccount extends AsyncTask<AccountEntity, Void, Void> {

    private Application application;
    private OnAsyncEventListener callback;
    private Exception exception;

    public CreateAccount(Application application, OnAsyncEventListener callback) {
        this.application = application;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(AccountEntity... params) {
        try {
            for (AccountEntity account : params)
                ((BaseApp) application).getDatabase().accountDao()
                        .insert(account);
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