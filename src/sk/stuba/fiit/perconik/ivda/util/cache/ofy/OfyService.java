package sk.stuba.fiit.perconik.ivda.util.cache.ofy;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by Seky on 9. 9. 2014.
 */
@ThreadSafe
public final class OfyService {

    static {
        factory().register(OfyBlob.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
