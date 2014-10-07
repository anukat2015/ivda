package sk.stuba.fiit.perconik.ivda.util.cache;

import java.io.Serializable;

/**
 * Created by Seky on 7. 10. 2014.
 */
public interface ICaching<Key, Value extends Serializable> {

    public boolean isCacheEnabled();

    public Value get(Key uri);

    public Value valueNotFound(Key key);
}
