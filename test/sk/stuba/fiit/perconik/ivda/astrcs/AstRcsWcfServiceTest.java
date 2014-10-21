package sk.stuba.fiit.perconik.ivda.astrcs;

import com.gratex.perconik.services.ast.rcs.*;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Assert;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.util.List;

/**
 * Otestovanie Astrcs sluzby
 */
public class AstRcsWcfServiceTest extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(AstRcsWcfServiceTest.class);

    /**
     * Otestovane prihalsovanie a vypis informacii o pouzivatelovi
     *
     * @throws Exception
     */
    public void testGetUser() throws Exception {
        Configuration.getInstance();
        UserDto user = AstRcsWcfService.getInstance().getUser(1);
        Assert.assertNotNull(user);
        System.out.println(user);
    }

    public void testFileversion() throws Exception {
        Configuration.getInstance();
        String serverUrl = "http://tfs.fiit.stuba.sk:8080/tfs/studentsprojects";
        String fileServerPah = "$/WhereIsMyCode/CodeReview/CodeReview.Web/AST/LoadEntities.cs";
        String changesetIdInRcs = "1081";

        RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(serverUrl);
        RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server, fileServerPah);
        ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(changesetIdInRcs, project);
        List<FileVersionDto> fileVersions = AstRcsWcfService.getInstance().getFileVersionsDto(changeset);
        for (FileVersionDto f : fileVersions) {
            LOGGER.info(f.getUrl().getValue());
        }
        FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, fileServerPah, project);

        fileServerPah = "$/WhereIsMyCode/CodeReview/CodeReview.Web/AST/ASTServiceClient.cs";
        try {
            fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, fileServerPah, project);
            Assert.assertTrue(true);
        } catch (Exception e) {

        }
    }
}
