package sk.stuba.fiit.perconik.ivda.cord.client;

/**
 * Created by Seky on 5. 9. 2014.
 */

import com.google.common.io.CharStreams;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.cord.dto.*;
import sk.stuba.fiit.perconik.ivda.cord.dto.File;
import sk.stuba.fiit.perconik.ivda.cord.entities.*;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.UriUtils;
import sk.stuba.fiit.perconik.ivda.util.cache.GuavaFilesCache;
import sk.stuba.fiit.perconik.ivda.util.cache.PersistentCache;
import sk.stuba.fiit.perconik.ivda.util.rest.Paged;
import sk.stuba.fiit.perconik.ivda.util.rest.RestClient;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by Seky on 22. 7. 2014.
 * <p>
 * Sluzba Cord. Pozri popis CordUserManual.docx.
 * <p>
 */
public class CordService extends RestClient {
    private static final Logger LOGGER = Logger.getLogger(CordService.class.getName());
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\/]");

    private final CordCache cache;
    private final FilesCache filesCache;

    private CordService() {
        cache = new CordCache();
        filesCache = new FilesCache();
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
        return UriBuilder.fromUri(Configuration.getInstance().getUacaLink());
    }

    public File getFile(String repo, String commit, String path) {
        return callApi(apiLink().path("file").path(repo).path(commit).path(path), File.class);
    }

    public List<File> getFiles(String repo, String commit, String path, FileSearchFilter filter) {
        return callApi(apiLink().path("files").path(repo).path(commit).path(path), FileSearchResult.class, filter);
    }

    public List<String> getFileBlob(FileDescription fd) {
        return filesCache.get(fd);
    }

    public AstParseResultDto getFileBlob2(String repo, String commit, String path) {
        return callApi(apiLink().path("blob").path(repo).path(commit).path(path).queryParam("format", "ast"), AstParseResultDto.class);
    }

    public Repository getRepository(String repo) {
        return callApi(apiLink().path("repo").path(repo), Repository.class);
    }

    public List<Repository> getRepositories(String repo) {
        return callApi(apiLink().path("repos").path(repo), RepoSearchResult.class, new SearchFilter());
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
        UriBuilder builder = apiLink().path("commits").path(repo).path(branch).path("head");
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
            return (List<T>) cache.getCache().get(new Pair<>(link, type));
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return Collections.emptyList();
        }
    }

    private List downloadAll(URI uri, Class type) {
        final List result = new ArrayList<>(150);
        getAllPages(uri, type, "pageIndex", new IProcessPage() {
            @Override
            public void downloaded(Paged response) {
                result.addAll(((SearchResult) response).getItems());
            }
        });
        return result;
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
    private final class CordCache extends GuavaFilesCache<Pair<URI, Class>, Serializable> {

        protected CordCache() {
            super(new java.io.File(Configuration.getInstance().getCacheFolder(), "cord"));
        }

        @Override
        protected Serializable fileNotFound(Pair<URI, Class> key) {
            return (Serializable) downloadAll(key.getKey(), key.getValue());
        }

        @Override
        protected java.io.File computeFilePath(java.io.File folder, Pair<URI, Class> key) {
            return new java.io.File(folder, computeUID(key.getKey().toString()));
        }
    }

    /**
     * Cache pre stahovanie suborov.
     */
    private final class FilesCache extends PersistentCache<FileDescription, ArrayList<String>> {
        protected FilesCache() {
            super(new java.io.File(Configuration.getInstance().getCacheFolder(), "cordFiles"));
        }

        @Override
        protected ArrayList<String> fileNotFound(FileDescription fd) {
            UriBuilder link = apiLink();
            link.path("blob").path(fd.getRepo()).path(fd.getCommit()).path(fd.getPath()).queryParam("format", "text");
            String content = callApi(link, String.class);
            try {
                return (ArrayList<String>) CharStreams.readLines(new StringReader(content));
            } catch (IOException e) {
                LOGGER.error("Can not deserialize.", e);
                return null;
            }
        }

        @Override
        protected ArrayList<String> deserialize(FileDescriptor fd) {
            try {
                return (ArrayList<String>) CharStreams.readLines(new BufferedReader(new FileReader(fd)));
            } catch (IOException e) {
                LOGGER.error("Can not deserialize.", e);
                return null;
            }
        }

        @Override
        protected void serialize(FileDescriptor fd, ArrayList<String> lines) {
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
