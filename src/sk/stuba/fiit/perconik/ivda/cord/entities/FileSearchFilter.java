package sk.stuba.fiit.perconik.ivda.cord.entities;

/**
 * Created by Seky on 5. 9. 2014.
 * Filter for file search request.
 */
public final class FileSearchFilter extends SearchFilter {
    private static final long serialVersionUID = 6665315077614075312L;

    private String suffixes; // file suffixes separated by pipes (|)
    private Boolean getLastCommit;

    public String getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(String suffixes) {
        this.suffixes = suffixes;
    }

    public Boolean getGetLastCommit() {
        return getLastCommit;
    }

    public void setGetLastCommit(Boolean getLastCommit) {
        this.getLastCommit = getLastCommit;
    }

    public void addSuffix(String suffixes) {
        this.suffixes = this.suffixes + '|' + suffixes;
    }
}
