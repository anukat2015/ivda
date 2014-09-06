package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nullable;

/**
 * Created by Seky on 6. 9. 2014.
 * Udaje dodavene do group sa nahradzuju inym retazcom.
 * Ochrana sukromia.
 */
public final class Developers {
    private static final BiMap<String, String> replaceGroup;
    private static char alphabetCurrent = 'D';

    static {
        replaceGroup = HashBiMap.create(16);
        replaceGroup.put("steltecia\\krastocny", "Developer A");
        replaceGroup.put("steltecia\\pzbell", "Developer B");
        replaceGroup.put("xchlebana", "Developer C");
    }

    public static String blackoutName(@Nullable String name) {
        if (name == null) {
            return name;
        }
        String groupnew = replaceGroup.get(name);
        if (groupnew == null) {
            groupnew = String.valueOf(alphabetCurrent);
            replaceGroup.put(name, groupnew);
            alphabetCurrent++;
        }
        return groupnew;
    }

    public static String getRealName(String name) {
        return replaceGroup.inverse().get(name);
    }
}
