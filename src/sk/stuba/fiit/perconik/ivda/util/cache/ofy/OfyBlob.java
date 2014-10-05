package sk.stuba.fiit.perconik.ivda.util.cache.ofy;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

import java.io.Serializable;

/**
 * Created by Seky on 9. 9. 2014.
 */
@Entity
public class OfyBlob implements Serializable {
    private static final long serialVersionUID = -8190447034986807932L;
    @Id
    private String id;

    @Serialize(zip = true)
    private Serializable data;

    public OfyBlob() {
    }

    public OfyBlob(String name, Serializable data) {
        id = name;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }
}
