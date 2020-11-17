package ch.hevs.students.raclettedb.util;

/**
 * This generic interface is used as custom callback for async tasks.
 */
public interface OnAsyncEventListener {
    void onSuccess();
    void onFailure(Exception e);
}
