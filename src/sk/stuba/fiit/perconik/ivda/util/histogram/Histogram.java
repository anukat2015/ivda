package sk.stuba.fiit.perconik.ivda.util.histogram;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;

/**
 * Created by Seky on 1. 11. 2014.
 * Linear histogram - increasing by one
 */
public abstract class Histogram<K extends Comparable<K>> {
    public void map(K key) {
        map(key, 1);
    }

    public abstract void map(K key, int count);

    public final List<Map.Entry<K, MutableInt>> reduce() {
        return reduce(true, true, false);
    }

    public abstract List<Map.Entry<K, MutableInt>> reduce(boolean sorted, boolean byKey, boolean reverse);

    protected static class KeyComparator<K extends Comparable<K>> implements Comparator<Map.Entry<K, MutableInt>> {
        @Override
        public int compare(Map.Entry<K, MutableInt> o1,
                           Map.Entry<K, MutableInt> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    }

    protected static class ValueComparator<K> implements Comparator<Map.Entry<K, MutableInt>> {
        @Override
        public int compare(Map.Entry<K, MutableInt> o1,
                           Map.Entry<K, MutableInt> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

    protected List<Map.Entry<K, MutableInt>> sort(List<Map.Entry<K, MutableInt>> colection, boolean sorted, boolean byKey, boolean reverse) {
        if (sorted) {
            Comparator comp;
            if (byKey) {
                comp = new KeyComparator();
            } else {
                comp = new ValueComparator<K>();
            }
            if (reverse) {
                comp = Collections.reverseOrder(comp);
            }
            Collections.sort(colection, comp);
        }
        return colection;
    }
}
