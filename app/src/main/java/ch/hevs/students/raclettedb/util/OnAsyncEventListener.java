package ch.hevs.students.raclettedb.util;

/**
 * This generic interface is used as custom callback for async tasks.
 * For an example usage see {@link ch.hevs.students.raclettedb.ui.mgmt.RegisterActivity:75}.
 */
public interface OnAsyncEventListener {
    void onSuccess();
    void onFailure(Exception e);
}
