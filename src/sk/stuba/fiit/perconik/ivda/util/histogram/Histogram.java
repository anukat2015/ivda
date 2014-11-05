package sk.stuba.fiit.perconik.ivda.util.histogram;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Seky on 1. 11. 2014.
 * Linear histogram - increasing by one
 */
public interface Histogram<K extends Comparable<K>> {
    void map(K key); // TODO: parameter should be Iterator<Entity>

    Iterator<Map.Entry<K, MutableInt>> reduce();
}
