package sk.stuba.fiit.perconik.ivda.util.histogram;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;

/**
 * Created by Seky on 5. 11. 2014.
 * Use this class for inserting keys in unsorted position.
 * Or for very alike keys.
 */
public class HistogramByHashTable<K extends Comparable<K>> extends Histogram<K> {

    private final Hashtable<K, MutableInt> map = new Hashtable();

    @Override
    public void map(K key, int n) {
        MutableInt finded = map.get(key);
        if (finded == null) {
            finded = new MutableInt(0);
            map.put(key, finded);
        }
        finded.add(n);
    }

    @Override
    public Collection<Map.Entry<K, MutableInt>> reduce(boolean sorted, boolean byKey, boolean reverse) {
        return sort(new ArrayList(map.entrySet()), sorted, byKey, reverse);
    }
}

