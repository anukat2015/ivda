package sk.stuba.fiit.perconik.ivda.util.histogram;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;

/**
 * Created by Seky on 5. 11. 2014.
 * HistogramByList is more memory efficient.
 * Use this class for keys inserting in sorted position!
 * Usable for distinct keys.
 */
public class HistogramByList<K extends Comparable<K>> implements Histogram<K> {

    private final List<Map.Entry<K, MutableInt>> keys = new ArrayList();

    private static final class MyEntry<K> implements Map.Entry<K, MutableInt> {
        private static final long serialVersionUID = 5517661263377263387L;
        private final K key;
        private int count;

        private MyEntry(K key) {
            this.key = key;
            count = 1;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public MutableInt getValue() {
            return new MutableInt(count);
        }

        @Override
        public MutableInt setValue(MutableInt value) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void map(K key) {
        Map.Entry<K, MutableInt> entry;
        int size = keys.size();
        if (size == 0) {
            entry = new MyEntry(key);
            keys.add(entry);
        } else {
            Map.Entry<K, MutableInt> before = keys.get(size - 1);
            if (before.getKey().equals(key)) {
                before.getValue().increment();
            } else {
                entry = new MyEntry(key);
                keys.add(entry);
            }
        }
    }

    @Override
    public Iterator<Map.Entry<K, MutableInt>> reduce() {
        return keys.iterator(); // they are all ready sorted
    }
}
