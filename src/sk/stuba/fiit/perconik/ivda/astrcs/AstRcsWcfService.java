package sk.stuba.fiit.perconik.ivda.astrcs;

import com.gratex.perconik.services.AstRcsWcfSvc;
import com.gratex.perconik.services.IAstRcsWcfSvc;
import com.gratex.perconik.services.ast.rcs.*;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.net.URI;
import java.util.List;

/**
 * Created by Seky on 6. 8. 2014.
 * Pomocna trieda sluzbu AstRcsWcf.
 * document.ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
 */
public final class AstRcsWcfService {
    private static final Logger logger = Logger.getLogger(AstRcsWcfService.class.getName());
    private final IAstRcsWcfSvc service;
    private final ObjectFactory factory;

    private AstRcsWcfService() {
        authenticate();
        service = new AstRcsWcfSvc().getPort(IAstRcsWcfSvc.class);
        factory = new ObjectFactory();
    }

    public static AstRcsWcfService getInstance() {
        return SingletonHolder.INSTANCE;
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

    protected void authenticate() {
        String username = Configuration.getInstance().getAstRcs().get("username");
        String password = Configuration.getInstance().getAstRcs().get("password");
        java.net.Authenticator.setDefault(new NtlmAuthenticator(username, password));
    }

    public UserDto getUser(Integer id) {
        GetUserRequest req = new GetUserRequest();
        req.setUserId(id);
        GetUserResponse response = service.getUser(req);
        return response.getUser().getValue();
    }

    public RcsServerDto getRcsServerDto(URI url) {
        SearchRcsServersRequest req = new SearchRcsServersRequest();
        req.setUrl(factory.createSearchRcsServersRequestUrl(url.toString()));
        SearchRcsServersResponse response = service.searchRcsServers(req);
        checkResponse(response);
        return returnOne(response.getRcsServers().getValue().getRcsServerDto());
    }

    private void checkResponse(PagedResponse res) {
        if (res == null) {
            throw new RuntimeException("PagedResponse is null");
        }
        if (res.getPageCount() != 1) {
            throw new RuntimeException("PagedResponse have more / less than one items.");
        }
    }

    public RcsProjectDto getRcsProjectDto(RcsServerDto server) {
        SearchRcsProjectsRequest req = new SearchRcsProjectsRequest();
        req.setRcsServerId(factory.createSearchRcsProjectsRequestRcsServerId(server.getId()));
        //req.setUrl();  // nazov projektu $/PerConIK
        // dokument $/PerConIK/ITGenerator/ITGenerator.Lib/Entities/ActivitySubjectOrObject.cs
        SearchRcsProjectsResponse response = service.searchRcsProjects(req);
        checkResponse(response);
        return returnOne(response.getRcsProjects().getValue().getRcsProjectDto());
    }

    // ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
    public ChangesetDto getChangesetDto(String changesetIdInRcs, RcsProjectDto project) {
        SearchChangesetsRequest req = new SearchChangesetsRequest();
        req.setChangesetIdInRcs(factory.createSearchChangesetsRequestChangesetIdInRcs(changesetIdInRcs));
        req.setRcsProjectId(project.getId());
        SearchChangesetsResponse response = service.searchChangesets(req);
        checkResponse(response);
        return returnOne(response.getChangesets().getValue().getChangesetDto());
    }

    public FileVersionDto getFileVersionDto(ChangesetDto chs, String serverPath, RcsProjectDto project) {
        // $/PerConIK
        // ITGenerator/ITGenerator.Lib/ActivitySvcCaller.cs
        String prefix = project.getUrl().getValue() + "/";
        if (!serverPath.startsWith(prefix)) {
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

    public String getFileContent(Integer fileVersion) {
        GetFileContentRequest req = new GetFileContentRequest();
        req.setVersionId(fileVersion);
        GetFileContentResponse response = service.getFileContent(req);
        return response.getContent().getValue();
    }

    public FileVersionDto getFile(Integer versionID) {
        GetFileRequest req = new GetFileRequest();
        req.setVersionId(versionID);
        GetFileResponse response = service.getFile(req);
        return response.getVersion().getValue();
    }

    public List<ChangesetDto> getChangeset(Integer entityID) {
        GetFileChangesetsRequest req = new GetFileChangesetsRequest();
        req.setEntityId(entityID);
        GetFileChangesetsResponse response = service.getFileChangesets(req);
        return response.getChangesets().getValue().getChangesetDto();
    }

    private static class SingletonHolder {
        public static final AstRcsWcfService INSTANCE = new AstRcsWcfService();
    }

    /*
            GetFilesByTfsIdentifiersRequest req = new GetFilesByTfsIdentifiersRequest();
        ArrayOfFileTfsIdentifierDto dto = new ArrayOfFileTfsIdentifierDto();
        req.setIdentifiers(factory.createGetFilesByTfsIdentifiersRequestIdentifiers(dto));
        GetFilesByTfsIdentifiersResponse response = service.getFilesByTfsIdentifiers(req)
        return returnOne(response.getVersions().getValue().getFileVersionDto());

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
    */
}

