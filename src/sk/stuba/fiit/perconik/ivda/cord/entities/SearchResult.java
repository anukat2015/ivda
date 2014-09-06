package sk.stuba.fiit.perconik.ivda.cord.entities;

import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.util.rest.Link;
import sk.stuba.fiit.perconik.ivda.util.rest.Paged;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seky on 5. 9. 2014.
 * SearchResult of something.
 */
public final class SearchResult<T extends Serializable> extends Paged {
    private static final long serialVersionUID = 3892748839161178943L;

    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resultSet", items).toString();
    }

    public boolean isHasNextPage() {
        for (Link link : getLinks()) {
            if (link.getRel().equals("next")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return getItems().isEmpty();
    }
}
