package sk.stuba.fiit.perconik.ivda.vcs.nepotrebne;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.gitective.core.BlobUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Seky on 21. 7. 2014.
 * https://github.com/kevinsawicki/gitective
 */
public class DiffTwoFiles {
    public static void main(String[] args) {
        Repository repo = null;
        try {
            repo = new FileRepository("/repos/jgit/.git");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectId current = BlobUtils.getId(repo, "master", "Main.java");
        ObjectId previous = BlobUtils.getId(repo, "master~1", "Main.java");

        Collection<Edit> edit = BlobUtils.diff(repo, previous, current);
    }
}
