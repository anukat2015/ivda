package sk.stuba.fiit.perconik.ivda.util.serialize;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;

/**
 * Created by Seky on 5. 11. 2014.
 */
public class ObjectInputIterator implements Iterator<Object> {
    private static final Logger LOGGER = Logger.getLogger(ObjectInputIterator.class.getName());
    private final ObjectInputStream in;
    private Object actual = null;
    private boolean empty = false;

    public ObjectInputIterator(InputStream in) throws IOException {
        this.in = new ObjectInputStream(in);
    }

    @Override
    public boolean hasNext() {
        if (empty) {
            return false; // for sure, if there are no more items, return  false for multiple calls
        }
        if (actual != null) {
            return true; //we have stored item
        }

        // if there is no stired item, try find another
        try {
            actual = in.readObject();
            if (actual instanceof IterativeOutputStream.EOF) {
                // that means there is no more items ...
                close();
                return false;
            }
            return true;
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.error("Cannot deserialize:", e);
            close();
        }
        return false;
    }

    protected void close() {
        empty = true;
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

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

