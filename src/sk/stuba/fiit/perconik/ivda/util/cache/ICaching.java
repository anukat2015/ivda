package sk.stuba.fiit.perconik.ivda.util.cache;

import java.io.Serializable;

/**
 * Created by Seky on 7. 10. 2014.
 */
public interface ICaching<Key, Value extends Serializable> {

    boolean isCacheEnabled();

    Value get(Key uri);

    Value valueNotFound(Key key);
}
