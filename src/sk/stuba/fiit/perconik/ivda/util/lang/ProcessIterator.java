package sk.stuba.fiit.perconik.ivda.util.lang;

import java.util.Iterator;

/**
 * Created by Seky on 21. 10. 2014.
 * Metoda na spracovania iteratora.
 */
public abstract class ProcessIterator<T> {
    private boolean enabledProcess;

    protected ProcessIterator() {
        enabledProcess = true;
    }

    public void proccess(Iterator<T> it) {
        started();
        while (enabledProcess && it.hasNext()) {
            proccessItem(it.next());
        }
        finished();
    }

    protected void stop() {
        enabledProcess = false;
    }

    protected abstract void proccessItem(T event);

    protected void finished() {
    }

    protected void started() {
    }
}
