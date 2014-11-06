package sk.stuba.fiit.perconik.ivda.util.lang;

import java.util.Iterator;

/**
 * Created by Seky on 6. 11. 2014.
 */
public abstract class FinishableIterator<E> implements Iterator<E> {
    private boolean empty = false;

    public abstract boolean _hasNext();

    @Override
    public final boolean hasNext() {
        if (empty) {
            return false; // for sure, if there are no more items, return  false for multiple calls
        }

        // if there is no stired item, try find another
        if (!_hasNext()) {
            finished();
            empty = true;
            return false;
        }
        return true;
    }

    protected void finished() {

    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
