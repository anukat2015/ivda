package sk.stuba.fiit.perconik.ivda.astrcs;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.gratex.perconik.services.AstRcsWcfSvc;
import com.gratex.perconik.services.IAstRcsWcfSvc;
import com.gratex.perconik.services.ast.rcs.*;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.Strings;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Seky on 6. 8. 2014.
 * Pomocna trieda sluzbu AstRcsWcf.
 * document.ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
 */
@ThreadSafe
public final class AstRcsWcfService {
    private static final Logger LOGGER = Logger.getLogger(AstRcsWcfService.class.getName());
    private static final File CACHE_FOLDER = new File(new File("C:/cache"), "astrcs");
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\/]");

    private final IAstRcsWcfSvc service;
    private final ObjectFactory factory;
    private final List<RcsServerDto> servers;

    private AstRcsWcfService() {
        authenticate();
        URL url = null;
        try {
            url = new URL("file:/" + Configuration.CONFIG_DIR + File.separator + "AstRcsWcfSvc.svc.wsdl");
        } catch (MalformedURLException e) {
            LOGGER.error("URL error", e);
        }
        service = new AstRcsWcfSvc(url).getPort(IAstRcsWcfSvc.class);
        /*String username = Configuration.getInstance().getAstRcs().get("username");
        String password = Configuration.getInstance().getAstRcs().get("password");

        Map<String, Object> map = ((BindingProvider) service).getRequestContext();
        map.put(BindingProvider.USERNAME_PROPERTY, username);
        map.put(BindingProvider.PASSWORD_PROPERTY, password);

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Username", Collections.singletonList(username));
        headers.put("Password", Collections.singletonList(password));
        headers.put("username", Collections.singletonList(username));
        headers.put("password", Collections.singletonList(password));
        headers.put("http.basic.username", Collections.singletonList(username));
        headers.put("http.basic.password", Collections.singletonList(password));
        map.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
        map.put(Stub.USERNAME_PROPERTY, "http.basic.username");
        map.put(Stub.PASSWORD_PROPERTY, "http.basic.password");
        map.put(BindingProvider.USERNAME_PROPERTY, "http.basic.username");
        map.put(BindingProvider.PASSWORD_PROPERTY, "http.basic.password");
        map.put("Authorization:", "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes()));
        */

        /*
        ((BindingProvider) service).getRequestContext().put(MessageContext
                .HTTP_REQUEST_HEADERS, Collections.singletonMap
                ("X-Client-Version", Collections.singletonList("1.0-RC")));

        ArrayList<javax.xml.ws.handler.Handler> list = new ArrayList();
        list.add(new SOAPHandler<SOAPMessageContext>() {

            @Override
            public boolean handleMessage(SOAPMessageContext context) {
                Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

                //if this is a request, true for outbound messages, false for inbound
                if (isRequest) {
                    try {
                        SOAPMessage soapMsg = context.getMessage();
                        SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
                        SOAPHeader soapHeader = soapEnv.getHeader();

                        //if no header, add one
                        if (soapHeader == null) {
                            soapHeader = soapEnv.addHeader();
                        }
                        soapHeader.
                    } catch (SOAPException e) {
                        System.err.println(e);
                    }
                }
                return true;
            }

            @Override
            public boolean handleFault(SOAPMessageContext context) {
                return true;
            }

            @Override
            public void close(MessageContext context) {

            }

            @Override
            public Set<QName> getHeaders() {
                return null;
            }
        });

        ((BindingProvider) service).getBinding().setHandlerChain(list);
         */

        factory = new ObjectFactory();
        try {
            servers = Collections.unmodifiableList(getRcsServersDto());
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
            LOGGER.error("Response have more pages, ignoring next pages.");
        }
    }


    private static String getName(String path, Integer versionID) {
        return FILE_NAME_PATTERN.matcher(path).replaceAll("_") + '-' + versionID;
    }

    private List<String> saveContent(String path, Integer id) throws NotFoundException {
        synchronized (CACHE_FOLDER) {
            File cacheFile = new File(CACHE_FOLDER, getName(path, id));
            try {
                if (!cacheFile.exists()) {
                    // Udaje prichdzaju zo specialnymi znakmy
                    String content = getFileContent(id);
                    Files.write(content, cacheFile, Charset.defaultCharset());
                    LOGGER.info("Ulozene do cache:" + cacheFile);
                }

                // Udaje vychadzaju bez specialnyzch znakov
                return Collections.unmodifiableList(Files.readLines(cacheFile, Charset.defaultCharset()));
            } catch (IOException e) {
                LOGGER.error("Nemozem precitat / zapisat zo suboru.", e);
                throw new NotFoundException("Nemozem precitat / zapisat zo suboru.");
            }
        }
    }

    /**
     * Download content of FileVersionDto with automatic caching.
     *
     * @param file FileVersionDto
     * @return list of lines
     * @throws NotFoundException when file cannot be downloaded or saved or do not exist
     */
    public List<String> getContent(FileVersionDto file) throws NotFoundException {
        try {
            return getContent(file.getUrl().getValue(), file.getId());
        } catch (NotFoundException e) {
            LOGGER.warn("Content probably not found for:" + file.getId() + " because " + file.getContentNotIncludedReason().value());
            throw e;
        }
    }

    public List<String> getContent(String path, Integer id) throws NotFoundException {
        Preconditions.checkArgument(path != null);
        Preconditions.checkArgument(id != null);
        return saveContent(path, id);
    }

    public List<String> getContentAncestor(FileVersionDto file) throws NotFoundException {
        Integer ancestor = file.getAncestor1Id().getValue();
        if (ancestor == null) {
            throw new NotFoundException("Ancestor is null");
        }
        return getContent(file.getUrl().getValue(), ancestor);
    }

    @SuppressWarnings("MethodMayBeStatic")
    private void authenticate() {
        String username = Configuration.getInstance().getAstRcs().get("username");
        String password = Configuration.getInstance().getAstRcs().get("password");
        Authenticator.setDefault(new NtlmAuthenticator(username, password));
    }

    public synchronized UserDto getUser(Integer id) {
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
    public RcsServerDto getNearestRcsServerDto(String url) throws NotFoundException {
        RcsServerDto server = Strings.findLongestPrefix(servers, url.toLowerCase(), new Function<RcsServerDto, String>() {
            @Nullable
            @Override
            public String apply(@Nullable RcsServerDto input) {
                return input.getUrl().getValue().toLowerCase();
            }
        });
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

    /**
     * Metoda na stiahnutie vsetkych stranok.
     *
     * @param req
     * @param func
     * @param <P>
     * @param <A>
     * @return
     */
    private static <P extends PagedRequest, A> List<A> downloadAll(String message, P req, Function<P, List<A>> func) throws NotFoundException {
        req.setPageSize(200);
        List<A> pole = new ArrayList<>(200);
        Integer index = 0;
        while (true) {
            req.setPageIndex(index);
            List<A> add = func.apply(req);
            if (add.isEmpty()) {
                if (index == 0) {
                    throw new NotFoundException("PagedResponse have no items at:" + message);
                }
                break;
            }
            pole.addAll(add);
            index++;
        }
        return pole;
    }

    public synchronized List<RcsServerDto> getRcsServersDto(@Nullable URI url) throws NotFoundException {
        SearchRcsServersRequest req = new SearchRcsServersRequest();
        if (url != null) {
            req.setUrl(factory.createSearchRcsServersRequestUrl(url.toString()));
        }

        List<RcsServerDto> ret = downloadAll("getRcsServersDto", req, new Function<SearchRcsServersRequest, List<RcsServerDto>>() {
            @Nullable
            @Override
            public List<RcsServerDto> apply(@Nullable SearchRcsServersRequest req) {
                SearchRcsServersResponse res = service.searchRcsServers(req);
                if (res.getPageCount() == 0) {
                    return Collections.emptyList();
                }
                return res.getRcsServers().getValue().getRcsServerDto();
            }
        });

        // Fixni nekonzistentne mena
        for (RcsServerDto server : ret) {
            server.getUrl().setValue(server.getUrl().getValue().toLowerCase());
        }
        return ret;
    }

    public synchronized RcsProjectDto getRcsProjectDto(RcsServerDto server, String serverPath) throws NotFoundException {
        SearchRcsProjectsRequest req = new SearchRcsProjectsRequest();
        req.setRcsServerId(factory.createSearchRcsProjectsRequestRcsServerId(server.getId()));
        req.setPageSize(1000);
        // req.setUrl();  // nazov projektu $/PerConIK
        // dokument $/PerConIK/ITGenerator/ITGenerator.Lib/Entities/ActivitySubjectOrObject.cs
        SearchRcsProjectsResponse response = service.searchRcsProjects(req);
        checkResponse(response, "getRcsProjectDto");

        List<RcsProjectDto> projects = response.getRcsProjects().getValue().getRcsProjectDto();
        RcsProjectDto project = Strings.findLongestPrefix(projects, serverPath.toLowerCase(), new Function<RcsProjectDto, String>() {
            @Nullable
            @Override
            public String apply(@Nullable RcsProjectDto input) {
                return input.getUrl().getValue().toLowerCase();
            }
        });

        if (project == null) {
            throw new NotFoundException("getRcsProjectDto");
        }
        return project;
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
    public synchronized ChangesetDto getChangesetDto(String changesetIdInRcs, RcsProjectDto project) throws NotFoundException {
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
            throw new RuntimeException("Prefix zadanej cesty a projektu nesedi.");
        }
        String startUrl = serverPath.substring(prefix.length(), serverPath.length());
        return returnOne(getFileVersionsDto(chs, startUrl, null));
    }

    public List<FileVersionDto> getFileVersionsDto(ChangesetDto chs) throws NotFoundException {
        return getFileVersionsDto(chs, null, null);
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
    public synchronized List<FileVersionDto> getFileVersionsDto(ChangesetDto chs, @Nullable String startUrl, @Nullable Integer entity) throws NotFoundException {
        SearchFilesRequest req = new SearchFilesRequest();
        req.setChangesetId(chs.getId());
        if (startUrl != null) {
            req.setUrlStart(factory.createSearchFilesRequestUrlStart(startUrl));
        }
        if (entity != null) {
            req.setEntityId(factory.createSearchFilesRequestEntityId(entity));
        }

        List<FileVersionDto> ret = downloadAll("getFileVersionsDto", req, new Function<SearchFilesRequest, List<FileVersionDto>>() {
            @Nullable
            @Override
            public List<FileVersionDto> apply(@Nullable SearchFilesRequest req) {
                SearchFilesResponse res = service.searchFiles(req);
                if (res.getPageCount() == 0) {
                    return Collections.emptyList();
                }
                return res.getFileVersions().getValue().getFileVersionDto();
            }
        });

        return ret;
    }

    public synchronized String getFileContent(Integer fileVersion) throws NotFoundException {
        GetFileContentRequest req = new GetFileContentRequest();
        req.setVersionId(fileVersion);
        GetFileContentResponse response = service.getFileContent(req);
        String value = response.getContent().getValue();
        if (value == null) {
            throw new NotFoundException("getFileContent content noit found");
        }
        return value;
    }

    public synchronized FileVersionDto getFile(Integer versionID) {
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
    public synchronized List<ChangesetDto> getChangesets(Integer entityID) {
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
    public synchronized List<FileVersionDto> getChangedFiles(ChangesetDto chs) {
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

        public NotFoundException(String msg) {
            super(msg);
        }
    }

    public ChangesetDto getChangesetSuccessor(ChangesetDto changeset, FileVersionDto file) throws NotFoundException {
        List<ChangesetDto> changesets = getChangesets(file.getEntityId());
        if (changesets == null || changesets.isEmpty()) {
            throw new NotFoundException("getChangesetSuccessor changeset empty");
        }
        Iterator<ChangesetDto> it = changesets.iterator();
        while (it.hasNext()) {
            ChangesetDto change = it.next();
            if (change.getId().equals(changeset.getId())) {
                if (it.hasNext()) {
                    return it.next();
                } else {
                    break;
                }
            }
        }
        throw new NotFoundException("getChangesetSuccessor not found");
    }

    public FileVersionDto getFileVersionSuccessor(ChangesetDto succesorchangeset, FileVersionDto file) throws NotFoundException {
        List<FileVersionDto> files = getFileVersionsDto(succesorchangeset, null, file.getEntityId());
        return returnOne(files);
        /*
         files = AstRcsWcfService.getInstance().getFileVersionsDto(ch, file.getUrl().getValue());
                for (FileVersionDto f : files) {
                    Integer ancestor = f.getAncestor1Id().getValue();
                    if (ancestor == null) {
                        continue;
                    }

                    if (ancestor.equals(file.getId())) {
                        successorChangeset = ch;
                        return f;
                    }
                }
                return null;
         */
    }
}

