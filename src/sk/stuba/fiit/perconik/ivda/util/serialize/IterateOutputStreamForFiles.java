package sk.stuba.fiit.perconik.ivda.util.serialize;

import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Seky on 6. 11. 2014.
 */
public class IterateOutputStreamForFiles implements Iterator<Object> {
    private static final Logger LOGGER = Logger.getLogger(IterateOutputStreamForFiles.class.getName());

    private final Iterator<File> fileIt;
    private Iterator<Object> currentEvents = null;

    // Should be sorted by date
    public IterateOutputStreamForFiles(List<File> files) {
        Collections.sort(files);
        fileIt = files.iterator();
    }

    @Override
    public boolean hasNext() {
        if (currentEvents != null) {
            if (currentEvents.hasNext()) {
                return true;
            } else {
                // Najdi dalsi ...
            }
        }

        while (true) {
            if (fileIt.hasNext()) {
                try {
                    currentEvents = GZIP.getIterativeInput(fileIt.next());
                } catch (IOException e) {
                    LOGGER.error("Cannot open:" + e);
                    return false;
                }
                if (currentEvents.hasNext()) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public Object next() {
        if (currentEvents == null) {
            return null;
        }
        return currentEvents.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
