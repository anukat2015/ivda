package sk.stuba.fiit.perconik.ivda;

import com.gratex.perconik.services.AstRcsWcfSvc;
import com.gratex.perconik.services.IAstRcsWcfSvc;
import com.gratex.perconik.services.ast.rcs.*;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.List;

/**
 * Created by Seky on 6. 8. 2014.
 * Pomocna trieda sluzbu AstRcsWcf.
 * document.ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
 */
public class AstRcsWcfService {
    private static final Logger logger = Logger.getLogger(AstRcsWcfService.class.getName());
    private static final IAstRcsWcfSvc service;
    private static final ObjectFactory factory;

    static {
        java.net.Authenticator.setDefault(new NtlmAuthenticator("steltecia\\PublicServices", "FiitSvc123."));
        service = new AstRcsWcfSvc().getPort(IAstRcsWcfSvc.class);
        factory = new ObjectFactory();
        //test();
    }

    private AstRcsWcfService() {
    }

    private static <T> T returnOne(List<T> items) {
        int size = items.size();
        if (size == 0) {
            throw new RuntimeException("List is empty");
        }
        if (size > 1) {
            throw new RuntimeException("List have more than one items.");
        }
        return items.get(0);
    }

    private static void test() {
        GetUserRequest req = new GetUserRequest();
        req.setUserId(1);
        GetUserResponse resposne = service.getUser(req);
        logger.info("U " + resposne.toString());
    }

    public static RcsServerDto getRcsServerDto(URI url) {
        SearchRcsServersRequest req = new SearchRcsServersRequest();
        req.setUrl(factory.createSearchRcsServersRequestUrl(url.toString()));
        SearchRcsServersResponse response = service.searchRcsServers(req);
        checkResponse(response);
        return returnOne(response.getRcsServers().getValue().getRcsServerDto());
    }

    private static void checkResponse(PagedResponse res) {
        if (res == null) {
            throw new RuntimeException("PagedResponse is null");
        }
        if (res.getPageCount() != 1) {
            throw new RuntimeException("PagedResponse have more than one items.");
        }
    }

    public static RcsProjectDto getRcsProjectDto(RcsServerDto server) {
        SearchRcsProjectsRequest req = new SearchRcsProjectsRequest();
        req.setRcsServerId(factory.createSearchRcsProjectsRequestRcsServerId(server.getId()));
        //req.setUrl();  // nazov projektu $/PerConIK
        // dokument $/PerConIK/ITGenerator/ITGenerator.Lib/Entities/ActivitySubjectOrObject.cs
        SearchRcsProjectsResponse response = service.searchRcsProjects(req);
        checkResponse(response);
        return returnOne(response.getRcsProjects().getValue().getRcsProjectDto());
    }

    // ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
    public static ChangesetDto getChangesetDto(String changesetIdInRcs, RcsProjectDto project) {
        SearchChangesetsRequest req = new SearchChangesetsRequest();
        req.setChangesetIdInRcs(factory.createSearchChangesetsRequestChangesetIdInRcs(changesetIdInRcs));
        req.setRcsProjectId(project.getId());
        SearchChangesetsResponse response = service.searchChangesets(req);
        checkResponse(response);
        return returnOne(response.getChangesets().getValue().getChangesetDto());
    }

    public static FileVersionDto getFileVersionDto(ChangesetDto chs, String serverPath, RcsProjectDto project) {
        // $/PerConIK
        // ITGenerator/ITGenerator.Lib/ActivitySvcCaller.cs
        String prefix = project.getUrl().getValue() + "/";
        if(!serverPath.startsWith(prefix)) {
            throw new RuntimeException();
        }
        String startUrl = serverPath.substring(prefix.length(), serverPath.length());
        SearchFilesRequest req = new SearchFilesRequest();
        req.setChangesetId(chs.getId());
        req.setUrlStart(factory.createSearchFilesRequestUrlStart(startUrl));
        SearchFilesResponse response = service.searchFiles(req);
        checkResponse(response);
        return returnOne(response.getFileVersions().getValue().getFileVersionDto());
    }

    public static String getFile(Integer fileVersion) {
        GetFileContentRequest req = new GetFileContentRequest();
        req.setVersionId(fileVersion);
        GetFileContentResponse response = service.getFileContent(req);
        return response.getContent().getValue();
    }

    /*
        try {
            SearchCodeEntitiesRequest req = new SearchCodeEntitiesRequest();
            req.setChangesetId( factory.createSearchCodeEntitiesRequestChangesetId(id) );
            SearchCodeEntitiesResponse response;
            response = service.searchCodeEntities(req);
            logger.info("SearchCodeEntitiesRequest " + response.getCodeEntityVersions().getValue().getCodeEntityVersionDto().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GetCodeEntityChangesetsRequest req3 = new GetCodeEntityChangesetsRequest();
            req3.setEntityId(2);
            GetCodeEntityChangesetsResponse response3;
            response3 = service.getCodeEntityChangesets(req3);
            logger.info("GetCodeEntityChangesetsRequest " + response3.getChangesets().getValue().getChangesetDto().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GetFileChangesetsRequest req3 = new GetFileChangesetsRequest();
            req3.setEntityId(2);
            GetFileChangesetsResponse response3;
            response3 = service.getFileChangesets(req3);
            logger.info("GetFileChangesetsRequest " + response3.getChangesets().getValue().getChangesetDto().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    */
}

