package sk.stuba.fiit.perconik.ivda.server.filestats;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 23. 10. 2014.
 *
 * Trieda umoznuje preidavat statistiky o uprave nad subormy.
 * Nasledne umoznujevyhladavat v tychto statistikach.
 */
public class EditsOfFiles {
    private final Map<String, PerUserChanges> map;

    public EditsOfFiles() {
        this.map = new HashMap<>();
    }

    public void add(String user, String file, Date date) {

    }

    private static class PerUserChanges {
        private final Map<String, PerUserChanges> map;

        private PerUserChanges() {
            this.map = new HashMap<>();
        }
    }
}
