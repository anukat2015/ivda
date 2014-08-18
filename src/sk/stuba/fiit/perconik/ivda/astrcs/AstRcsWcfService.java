package sk.stuba.fiit.perconik.ivda.astrcs;

import com.gratex.perconik.services.AstRcsWcfSvc;
import com.gratex.perconik.services.IAstRcsWcfSvc;
import com.gratex.perconik.services.ast.rcs.*;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.Strings;

import javax.annotation.Nullable;
import java.net.Authenticator;
import java.net.URI;
import java.util.List;

/**
 * Created by Seky on 6. 8. 2014.
 * Pomocna trieda sluzbu AstRcsWcf.
 * document.ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
 */
public final class AstRcsWcfService {
    private static final Logger LOGGER = Logger.getLogger(AstRcsWcfService.class.getName());
    private final IAstRcsWcfSvc service;
    private final ObjectFactory factory;
    private final List<RcsServerDto> servers;

    private AstRcsWcfService() {
        authenticate();
        service = new AstRcsWcfSvc().getPort(IAstRcsWcfSvc.class);
        factory = new ObjectFactory();
        try {
            servers = getRcsServersDto();
            LOGGER.info("Logged in.");
        } catch (NotFoundException e) {
            throw new RuntimeException("No servers found. Connected?");
        }
    }

    @SuppressWarnings("SameReturnValue")
    public static AstRcsWcfService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static <T> T returnOne(List<T> items) throws NotFoundException {
        int size = items.size();
        if (size == 0) {
            throw new NotFoundException("List is empty");
        }
        if (size > 1) {
            throw new RuntimeException("List have more than one items.");
        }
        return items.get(0);
    }

    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    private static void checkResponse(@Nullable PagedResponse res, String message) throws NotFoundException {
        if (res == null) {
            throw new RuntimeException("PagedResponse is null");
        }
        if (res.getPageCount() == 0) {
            throw new NotFoundException("PagedResponse have no items at:" + message);
        }
        if (res.getPageCount() > 1) {
            // TODO: neimplementovat stahovanie dalsich stran, pockat kym sluzba prejde na REST
            LOGGER.warn("Response have more pages, ignoring next pages.");
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    private void authenticate() {
        String username = Configuration.getInstance().getAstRcs().get("username");
        String password = Configuration.getInstance().getAstRcs().get("password");
        Authenticator.setDefault(new NtlmAuthenticator(username, password));
    }

    public UserDto getUser(Integer id) {
        GetUserRequest req = new GetUserRequest();
        req.setUserId(id);
        GetUserResponse response = service.getUser(req);
        return response.getUser().getValue();
    }

    /**
     * Hladaj server, ktory je najblizssi k nasmu.
     * Hladas https://github.com/perconik/perconik.git a najde  https://github.com.
     *
     * @param url
     * @return
     */
    public RcsServerDto getNearestRcsServerDto(URI url) throws NotFoundException {
        RcsServerDto server = Strings.findLongestPrefix(servers, url.toString(), input -> input.getUrl().getValue());
        if (server == null) {
            throw new NotFoundException("getNearestRcsServerDto");
        }
        return server;
    }

    public RcsServerDto getRcsServerDto(URI url) throws NotFoundException {
        return returnOne(getRcsServersDto(url));
    }

    public List<RcsServerDto> getRcsServersDto() throws NotFoundException {
        return getRcsServersDto(null);
    }

    public List<RcsServerDto> getRcsServersDto(@Nullable URI url) throws NotFoundException {
        SearchRcsServersRequest req = new SearchRcsServersRequest();
        if (url != null) {
            req.setUrl(factory.createSearchRcsServersRequestUrl(url.toString()));
        }
        SearchRcsServersResponse response = service.searchRcsServers(req);
        checkResponse(response, "getRcsServersDto");
        return response.getRcsServers().getValue().getRcsServerDto();
    }

    public RcsProjectDto getRcsProjectDto(RcsServerDto server) throws NotFoundException {
        SearchRcsProjectsRequest req = new SearchRcsProjectsRequest();
        req.setRcsServerId(factory.createSearchRcsProjectsRequestRcsServerId(server.getId()));
        //req.setUrl();  // nazov projektu $/PerConIK
        // dokument $/PerConIK/ITGenerator/ITGenerator.Lib/Entities/ActivitySubjectOrObject.cs
        SearchRcsProjectsResponse response = service.searchRcsProjects(req);
        checkResponse(response, "getRcsProjectDto");
        return returnOne(response.getRcsProjects().getValue().getRcsProjectDto());
    }

    /**
     * ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
     * Prekonvertuj changesetIdInRcs na takzvane ChangesetDto, ktore bude obsahovat Changeset ID.
     *
     * @param changesetIdInRcs
     * @param project
     * @return
     * @throws NotFoundException
     */
    public ChangesetDto getChangesetDto(String changesetIdInRcs, RcsProjectDto project) throws NotFoundException {
        SearchChangesetsRequest req = new SearchChangesetsRequest();
        req.setChangesetIdInRcs(factory.createSearchChangesetsRequestChangesetIdInRcs(changesetIdInRcs));
        req.setRcsProjectId(project.getId());
        SearchChangesetsResponse response = service.searchChangesets(req);
        checkResponse(response, "getChangesetDto");
        return returnOne(response.getChangesets().getValue().getChangesetDto());
    }

    public FileVersionDto getFileVersionDto(ChangesetDto chs, String serverPath, RcsProjectDto project) throws NotFoundException {
        // $/PerConIK
        // ITGenerator/ITGenerator.Lib/ActivitySvcCaller.cs
        //noinspection HardcodedFileSeparator
        String prefix = project.getUrl().getValue() + '/';
        if (!serverPath.startsWith(prefix)) {
            throw new RuntimeException("Prefix zadanej cedzy a projektu nesedi.");
        }
        String startUrl = serverPath.substring(prefix.length(), serverPath.length());
        return returnOne(getFileVersionsDto(chs, startUrl));
    }

    public List<FileVersionDto> getFileVersionsDto(ChangesetDto chs) throws NotFoundException {
        return getFileVersionsDto(chs, null);
    }

    /**
     * Gets all file versions in the specified changeset matching given filter
     * Vrati zoznam vsetkych suborov zmenenych ci nezmenenych pre dany changeset.
     *
     * @param chs
     * @param startUrl
     * @return
     * @throws NotFoundException
     */
    public List<FileVersionDto> getFileVersionsDto(ChangesetDto chs, @Nullable String startUrl) throws NotFoundException {
        SearchFilesRequest req = new SearchFilesRequest();
        req.setChangesetId(chs.getId());
        if (startUrl != null) {
            req.setUrlStart(factory.createSearchFilesRequestUrlStart(startUrl));
        }
        SearchFilesResponse response = service.searchFiles(req);
        checkResponse(response, "getFileVersionsDto");
        return response.getFileVersions().getValue().getFileVersionDto();
    }

    public String getFileContent(Integer fileVersion) throws NotFoundException {
        GetFileContentRequest req = new GetFileContentRequest();
        req.setVersionId(fileVersion);
        GetFileContentResponse response = service.getFileContent(req);
        String value = response.getContent().getValue();
        if (value == null) {
            throw new NotFoundException();
        }
        return value;
    }

    public FileVersionDto getFile(Integer versionID) {
        GetFileRequest req = new GetFileRequest();
        req.setVersionId(versionID);
        GetFileResponse response = service.getFile(req);
        return response.getVersion().getValue();
    }

    /**
     * Pre dany subor vypis zoznam changesetov.
     *
     * @param entityID
     * @return
     */
    public List<ChangesetDto> getChangeset(Integer entityID) {
        GetFileChangesetsRequest req = new GetFileChangesetsRequest();
        req.setEntityId(entityID);
        GetFileChangesetsResponse response = service.getFileChangesets(req);
        return response.getChangesets().getValue().getChangesetDto();
    }

    /**
     * Ukaze, ktore subory sa zmenili v danom changesete.
     *
     * @param chs
     * @return
     */
    public List<FileVersionDto> getChangedFiles(ChangesetDto chs) {
        GetChangedFilesRequest req = new GetChangedFilesRequest();
        req.setChangesetId(chs.getId());
        GetChangedFilesResponse response = service.getChangedFiles(req);
        return response.getFileVersions().getValue().getFileVersionDto();
    }

    private static class SingletonHolder {
        public static final AstRcsWcfService INSTANCE = new AstRcsWcfService();
    }

    public static class NotFoundException extends Exception {
        private static final long serialVersionUID = 0L;

        public NotFoundException() {
        }

        public NotFoundException(String msg) {
            super(msg);
        }
    }
}

