package sk.stuba.fiit.perconik.ivda.util.serialize;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.lang.FinishableIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by Seky on 5. 11. 2014.
 */
public class ObjectInputIterator extends FinishableIterator<Object> {
    private static final Logger LOGGER = Logger.getLogger(ObjectInputIterator.class.getName());
    private final ObjectInputStream in;
    private Object actual = null;

    public ObjectInputIterator(InputStream in) throws IOException {
        this.in = new ObjectInputStream(in);
    }

    @Override
    public boolean _hasNext() {
        if (actual != null) {
            return true; //we have stored item
        }

        // if there is no stired item, try find another
        try {
            actual = in.readObject();
            if (actual instanceof IterateOutputStream.EOF) {
                // that means there is no more items ...
                return false;
            }
            return true;
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error("Cannot deserialize:", e);
        }
        return false;
    }

    @Override
    protected void finished() {
        try {
            in.close();
        } catch (IOException e) {
            LOGGER.error("Cannot close:", e);
        }
    }

    @Override
    public Object next() {
        Object ret = actual;
        actual = null; // remove stored item
        return ret;
    }
}

