package sk.stuba.fiit.perconik.ivda.activity.client;

import sk.stuba.fiit.perconik.ivda.activity.entities.PagedResponse;

import java.io.Serializable;

/**
 * Created by Seky on 1. 9. 2014.
 * Trieda pre spracovavanie stiahnutych prvkov.
 */
public interface IProcessDownloaded<T extends Serializable> {
    /**
     * Spracuj odpoved vo vlastnej metode.
     *
     * @param response
     * @return true - pokracuj v stahovani dalej
     */
    public boolean isDownloaded(PagedResponse<T> response);

    public default void cancelled() {
    }

    public default void finished() {
    }
}
