package sk.stuba.fiit.perconik.ivda.cord.client;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.cord.dto.*;
import sk.stuba.fiit.perconik.ivda.cord.entities.CommitSearchFilter;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.util.List;

/**
 * Otestovanie CORD sluzby
 */
public class CordServiceTest extends TestCase {
    protected static final Logger LOGGER = Logger.getLogger(CordServiceTest.class.getName());
    private static final String ZAUJIMAVY_SUBOR = "sk.stuba.fiit.perconik.eclipse/src/sk/stuba/fiit/perconik/eclipse/jdt/core/JavaElementEventType.java";
    private static final String REPOSITORE = "perconik_zbell";
    private static final String BRANCH = "legacy";
    private static final FileDescription FD = new FileDescription(REPOSITORE, BRANCH, ZAUJIMAVY_SUBOR);


    public void testGetRepositories() throws Exception {
        Configuration.getInstance();
        LOGGER.info(CordService.getInstance().getRepositories());
    }

    public void testBranches() throws Exception {
        Configuration.getInstance();
        List<Repository> repositories = CordService.getInstance().getRepositories();
        for (Repository rep : repositories) {
            List<Branch> branches = CordService.getInstance().getBranches(rep.getName());
            LOGGER.info(branches);
        }
    }

    public void testCommit() throws Exception {
        Configuration.getInstance();
        List<Commit> commits = CordService.getInstance().getCommits(REPOSITORE, BRANCH, null, new CommitSearchFilter());
        LOGGER.info(commits);
    }

    public void testCommits() throws Exception {
        Configuration.getInstance();
        List<Repository> repositories = CordService.getInstance().getRepositories();
        for (Repository rep : repositories) {
            List<Branch> branches = CordService.getInstance().getBranches(rep.getName());
            for (Branch branch : branches) {
                CommitSearchFilter filter = new CommitSearchFilter();
                List<Commit> commits = CordService.getInstance().getCommits(rep.getName(), branch.getName(), null, filter);
                LOGGER.info(commits);
            }
        }
    }

    public void testFile() throws Exception {
        Configuration.getInstance();
        File file = CordService.getInstance().getFile(FD);
        LOGGER.info(file);
        List<String> content = CordService.getInstance().getFileBlob(FD);
        LOGGER.info(content);
        AstParseResultDto data = CordService.getInstance().getFileBlob2(FD);
        LOGGER.info(data);
    }
}
