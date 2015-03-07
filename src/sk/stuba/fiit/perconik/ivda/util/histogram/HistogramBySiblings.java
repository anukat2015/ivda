package sk.stuba.fiit.perconik.ivda.util.histogram;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;

/**
 * Created by Seky on 5. 11. 2014.
 * HistogramByList is more memory efficient.
 * Use this class for keys inserting in sorted position!
 * Usable for distinct keys.
 */
public class HistogramBySiblings<K extends Comparable<K>> extends Histogram<K> {

    private final List<Map.Entry<K, MutableInt>> keys = new ArrayList();

    private static final class MyEntry<K> implements Map.Entry<K, MutableInt> {
        private static final long serialVersionUID = 5517661263377263387L;
        private final K key;
        private MutableInt count;

        private MyEntry(K key, int n) {
            this.key = key;
            count = new MutableInt(n);
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public MutableInt getValue() {
            return count;
        }

        @Override
        public MutableInt setValue(MutableInt value) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void map(K key, int count) {
        Map.Entry<K, MutableInt> entry;
        int size = keys.size();
        if (size == 0) {
            entry = new MyEntry(key, count);
            keys.add(entry);
        } else {
            Map.Entry<K, MutableInt> before = keys.get(size - 1);
            if (before.getKey().equals(key)) {
                before.getValue().add(count);
            } else {
                entry = new MyEntry(key, count);
                keys.add(entry);
            }
        }
    }

    @Override
    public List<Map.Entry<K, MutableInt>> reduce(boolean sorted, boolean byKey, boolean reverse) {
        return sort(keys, sorted, byKey, reverse);
    }
}
