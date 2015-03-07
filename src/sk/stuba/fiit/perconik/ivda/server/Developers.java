package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Seky on 6. 9. 2014.
 * Udaje dodavene do group sa nahradzuju inym retazcom.
 * Ochrana sukromia.
 */
@Singleton
@ThreadSafe
public final class Developers {
    private final BiMap<String, String> replaceGroup;

    private Developers() {
        replaceGroup = HashBiMap.create(16);
        replaceGroup.putAll(Configuration.getInstance().getDevelopers());
    }

    private static class DevelopersHolder {
        private static final Developers INSTANCE = new Developers();
    }

    public static Developers getInstance() {
        return DevelopersHolder.INSTANCE;
    }

    public String getRealName(String name) throws IllegalArgumentException {
        String real = replaceGroup.get(name);
        if (real == null) {
            throw new IllegalArgumentException();
        }
        return real;
    }

    public Set<String> getRealNames() {
        return Collections.unmodifiableSet(replaceGroup.keySet());
    }

}
