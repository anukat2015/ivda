package sk.stuba.fiit.perconik.ivda.cord.dto;

import java.io.Serializable;

/**
 * Created by Seky on 5. 9. 2014.
 * Pomocna trieda pre opis cesty a verzie suboru.
 */
public class FileDescription implements Serializable {
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
}
