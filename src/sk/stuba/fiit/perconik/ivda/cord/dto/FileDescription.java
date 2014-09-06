package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 * Pomocna trieda pre opis cesty a verzie suboru.
 */
public final class FileDescription implements Serializable {
    private String repo;
    private String commit;
    private String path;

    public FileDescription(String repo, String commit, String path) {
        this.repo = repo;
        this.commit = commit;
        this.path = path;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDescription that = (FileDescription) o;

        if (commit != null ? !commit.equals(that.commit) : that.commit != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (repo != null ? !repo.equals(that.repo) : that.repo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = repo != null ? repo.hashCode() : 0;
        result = 31 * result + (commit != null ? commit.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
