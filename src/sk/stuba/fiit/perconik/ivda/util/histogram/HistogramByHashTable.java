package sk.stuba.fiit.perconik.ivda.util.histogram;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;

/**
 * Created by Seky on 5. 11. 2014.
 * Use this class for inserting keys in unsorted position.
 * Or for very alike keys.
 */
public class HistogramByHashTable<K extends Comparable<K>> implements Histogram<K> {

    private final Hashtable<K, MutableInt> map = new Hashtable();

    private List<Map.Entry<K, MutableInt>> sort() {
        ArrayList<Map.Entry<K, MutableInt>> zoznam;
        zoznam = new ArrayList(map.entrySet());
        Comparator<Map.Entry<K, MutableInt>> com = new Comparator<Map.Entry<K, MutableInt>>() {

            @Override
            public int compare(Map.Entry<K, MutableInt> o1,
                               Map.Entry<K, MutableInt> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        };
        Collections.sort(zoznam, com);
        return zoznam;
    }

    @Override
    public void map(K key) {
        MutableInt finded = map.get(key);
        if (finded == null) {
            finded = new MutableInt();
            map.put(key, finded);
        }
        finded.increment();
    }

    @Override
    public Iterator<Map.Entry<K, MutableInt>> reduce() {
        return sort().iterator();
    }
}

