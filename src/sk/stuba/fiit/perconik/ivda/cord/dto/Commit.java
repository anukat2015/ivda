package sk.stuba.fiit.perconik.ivda.cord.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seky on 5. 9. 2014.
 */
public final class Commit implements Serializable {
    private String hash;
    private String message;
    private Date commitDate;
    private String ancestor1;
    private String ancestor2;
    private Author author;

    public Commit() {
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getAncestor1() {
        return ancestor1;
    }

    public void setAncestor1(String ancestor1) {
        this.ancestor1 = ancestor1;
    }

    public String getAncestor2() {
        return ancestor2;
    }

    public void setAncestor2(String ancestor2) {
        this.ancestor2 = ancestor2;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Commit commit = (Commit) o;

        if (hash != null ? !hash.equals(commit.hash) : commit.hash != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash != null ? hash.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ancestor1", ancestor1).append("ancestor2", ancestor2).append("author", author).append("commitDate", commitDate).append("hash", hash).append("message", message).toString();
    }
}
