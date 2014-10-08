package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * Created by Seky on 6. 9. 2014.
 * Udaje dodavene do group sa nahradzuju inym retazcom.
 * Ochrana sukromia.
 */
@ThreadSafe
public final class Developers {
    private final BiMap<String, String> replaceGroup;
    private char alphabetCurrent = 'A';

    private Developers() {
        replaceGroup = HashBiMap.create(16);
        List<String> developers = Configuration.getInstance().getDevelopers().getList();
        for (String developer : developers) {
            generateName(developer);
        }
    }

    private static class DevelopersHolder {
        private static final Developers INSTANCE = new Developers();
    }

    public static Developers getInstance() {
        return DevelopersHolder.INSTANCE;
    }

    /**
     * Prislo nedefinovane nove meno ...
     *
     * @param name
     * @return
     */
    private String generateName(String name) {
        String groupnew = "Developer " + alphabetCurrent;
        replaceGroup.put(name, groupnew);
        alphabetCurrent++;
        return groupnew;
    }

    public String blackoutName(@Nullable String name) {
        if (name == null) {
            return name;
        }
        if (!Configuration.getInstance().getBlackout()) {
            return name;
        }
        String groupnew;
        synchronized (replaceGroup) {
            groupnew = replaceGroup.get(name);
            if (groupnew == null) {
                // Prislo nedefinovane nove meno ...
                groupnew = generateName(name);
            }
        }
        return groupnew;
    }

    public String getRealName(String name) {
        String real;
        synchronized (replaceGroup) {
            real = replaceGroup.inverse().get(name);
        }
        return real;
    }
}
