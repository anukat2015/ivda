package sk.stuba.fiit.perconik.ivda.cord.client;

/**
 * Created by Seky on 5. 9. 2014.
 */

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.cord.dto.*;
import sk.stuba.fiit.perconik.ivda.cord.dto.File;
import sk.stuba.fiit.perconik.ivda.cord.entities.*;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.Strings;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;
import sk.stuba.fiit.perconik.ivda.util.cache.GuavaFilesCache;
import sk.stuba.fiit.perconik.ivda.util.cache.PersistentCache;
import sk.stuba.fiit.perconik.ivda.util.rest.RestClient;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by Seky on 22. 7. 2014.
 * <p/>
 * Sluzba Cord. Pozri popis CordUserManual.docx.
 * <p/>
 */
public final class CordService extends RestClient {
    private static final Logger LOGGER = Logger.getLogger(CordService.class.getName());
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\/]");
    private final List<Repository> repositories;


    private final CordCache cache;
    private final FilesCache filesCache;

    private CordService() {
        cache = new CordCache();
        filesCache = new FilesCache();
        repositories = Collections.unmodifiableList(getRepositories());
    }

    @SuppressWarnings("SameReturnValue")
    public static CordService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static String getFileName(String path, Integer versionID) {
        return FILE_NAME_PATTERN.matcher(path).replaceAll("_") + '-' + versionID;
    }

    @Override
    protected UriBuilder apiLink() {
        return UriBuilder.fromUri(Configuration.getInstance().getCordLink());
    }

    /**
     * ZVLASTNOST: fd.commit moze obsahovat aj BRANCH name a vrati rovnaky subor
     *
     * @param fd
     * @return
     */
    public File getFile(FileDescription fd) {
        return callApi(apiLink().path("file").path(fd.getRepo()).path(fd.getCommit()).path(fd.getPath()), File.class);
    }

    public List<File> getFiles(String repo, String commit, String path, FileSearchFilter filter) {
        return callApi(apiLink().path("files").path(repo).path(commit).path(path), FileSearchResult.class, filter);
    }

    public List<String> getFileBlob(FileDescription fd) {
        return filesCache.get(fd);
    }

    public AstParseResultDto getFileBlob2(FileDescription fd) {
        return callApi(apiLink().path("blob").path(fd.getRepo()).path(fd.getCommit()).path(fd.getPath()).queryParam("format", "ast"), AstParseResultDto.class);
    }

    public Repository getRepository(String repo) {
        return callApi(apiLink().path("repo").path(repo), Repository.class);
    }

    public List<Repository> getRepositories() {
        return callApi(apiLink().path("repos"), RepoSearchResult.class, new SearchFilter());
    }

    public Repository getNearestRepository(URI url) {
        Repository server = Strings.findLongestPrefix(repositories, url.toString().toLowerCase(), new Function<Repository, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Repository input) {
                return input.getUrl().toString();
            }
        });
        return server;
    }

    public List<Branch> getBranches(String repo) {
        return callApi(apiLink().path("branches").path(repo), BranchSearchResult.class, new SearchFilter());
    }

    public Commit getCommit(String repo, String commit) {
        return callApi(apiLink().path("commit").path(repo).path(commit), Commit.class);
    }

    /**
     * Get information about head commit (the latest commit in the repository branch)
     *
     * @param repo
     * @param branch
     * @return
     */
    public Commit getHeadCommit(String repo, String branch) {
        return callApi(apiLink().path("commit").path(repo).path(branch).path("head"), Commit.class);
    }

    public List<Commit> getCommits(String repo, String branch, @Nullable String path, CommitSearchFilter filter) {
        UriBuilder builder = apiLink().path("commits").path(repo).path(branch);
        if (path != null) {
            builder.path(path);
        }
        return callApi(builder, CommitSearchResult.class, filter);
    }

    /**
     * Get list offiles changed in given commit
     *
     * @param repo
     * @param commit
     * @return
     */
    public ChangeSearchResult getChanges(String repo, String commit) {
        return callApi(apiLink().path("changes").path(repo).path(commit), ChangeSearchResult.class);
    }

    /**
     * Ziada sa zoznam niecoho.
     *
     * @param path
     * @param type
     * @param filter
     * @param <T>
     * @return
     */
    private <T extends Serializable> List<T> callApi(UriBuilder path, Class<?> type, SearchFilter filter) {
        // Iba zoznamy cachujeme
        try {
            URI link = UriUtils.addBeanProperties(path, filter).build();
            ImmutableList<T> list = (ImmutableList<T>) cache.getCache().get(new Pair<URI, Class>(link, type));
            return list;
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return Collections.emptyList();
        }
    }

    /**
     * Ziada sa jeden prvok.
     *
     * @param path
     * @param type
     * @param <T>
     * @return
     */
    private <T> T callApi(UriBuilder path, Class<?> type) {
        return (T) downloadUri(path.build(), type);
    }

    private static class SingletonHolder {
        public static final CordService INSTANCE = new CordService();
    }

    /**
     * Cache pre requesty kde sa ziadaju zoznamy
     */
    private final class CordCache extends GuavaFilesCache<Pair<URI, Class>, ImmutableList> {

        protected CordCache() {
            super(new java.io.File(Configuration.getInstance().getCacheFolder(), "cord"));
        }

        @Override
        protected ImmutableList valueNotFound(Pair<URI, Class> key) {
            return downloadAll(key.getKey(), key.getValue(), "pageIndex");
        }

        @Override
        protected java.io.File computeFilePath(java.io.File folder, Pair<URI, Class> key) {
            return new java.io.File(folder, computeUID(key.getKey().toString()));
        }
    }

    /**
     * Cache pre stahovanie suborov.
     */
    private final class FilesCache extends PersistentCache<FileDescription, ImmutableList<String>> {
        protected FilesCache() {
            super(new java.io.File(Configuration.getInstance().getCacheFolder(), "cordFiles"));
        }

        @Override
        protected ImmutableList<String> valueNotFound(FileDescription fd) {
            UriBuilder link = apiLink();
            link.path("blob").path(fd.getRepo()).path(fd.getCommit()).path(fd.getPath()).queryParam("format", "text");
            String content = callApi(link, String.class);
            try {
                return ImmutableList.copyOf(CharStreams.readLines(new StringReader(content)));
            } catch (IOException e) {
                LOGGER.error("Can not deserialize.", e);
                return null;
            }
        }

        @Override
        protected ImmutableList<String> deserialize(FileDescriptor fd) {
            try {
                return ImmutableList.copyOf(CharStreams.readLines(new BufferedReader(new FileReader(fd))));
            } catch (IOException e) {
                LOGGER.error("Can not deserialize.", e);
                return null;
            }
        }

        @Override
        protected void serialize(FileDescriptor fd, ImmutableList<String> lines) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fd));
                for (CharSequence line : lines) {
                    writer.append(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                LOGGER.error("Can not serialize.", e);
            }
        }
    }

}
