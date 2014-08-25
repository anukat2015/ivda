package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Seky on 18. 8. 2014.
 * <p>
 * Pomocne utilitky pre praci s roznymi verziami suborov.
 */
public final class FileVersionsUtil {
    private static final Logger LOGGER = Logger.getLogger(FileVersionsUtil.class.getName());
    private static final File CACHE_FOLDER = Configuration.getInstance().getCacheFolder();
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[\\/]");

    private static String getName(String path, Integer versionID) {
        return FILE_NAME_PATTERN.matcher(path).replaceAll("_") + '-' + versionID;
    }

    private static List<String> saveContent(String path, Integer id) throws AstRcsWcfService.NotFoundException {
        File cacheFile = new File(CACHE_FOLDER, getName(path, id));
        try {
            if (!cacheFile.exists()) {
                // Udaje prichdzaju zo specialnymi znakmy
                String content = AstRcsWcfService.getInstance().getFileContent(id);
                Files.write(content, cacheFile, Charset.defaultCharset());
                LOGGER.info("Ulozene do cache:" + cacheFile);
            }

            // Udaje vychadzaju bez specialnyzch znakov
            return Collections.unmodifiableList(Files.readLines(cacheFile, Charset.defaultCharset()));
        } catch (IOException e) {
            LOGGER.error("Nemozem precitat / zapisat zo suboru.", e);
            throw new AstRcsWcfService.NotFoundException();
        }
    }

    /**
     * Download content of FileVersionDto with automatic caching.
     *
     * @param file FileVersionDto
     * @return list of lines
     * @throws AstRcsWcfService.NotFoundException when file cannot be downloaded or saved or do not exist
     */
    public static List<String> getContent(FileVersionDto file) throws AstRcsWcfService.NotFoundException {
        try {
            return getContent(file.getUrl().getValue(), file.getId());
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.warn("Content probably not found for:" + file.getId() + " because " + file.getContentNotIncludedReason().value());
            throw e;
        }
    }

    public static List<String> getContent(String path, Integer id) throws AstRcsWcfService.NotFoundException {
        Preconditions.checkArgument(path != null);
        Preconditions.checkArgument(id != null);
        return saveContent(path, id);
    }

    public static List<String> getContentAncestor(FileVersionDto file) throws AstRcsWcfService.NotFoundException {
        Integer ancestor = file.getAncestor1Id().getValue();
        if (ancestor == null) {
            throw new AstRcsWcfService.NotFoundException("Ancestor is null");
        }
        return getContent(file.getUrl().getValue(), ancestor);
    }


    public static class DiffStats {
        Integer additions = 0;
        Integer deletions = 0;
    }

    /**
     * Dany subor sme nasli, zachyt ID, vypis hodnotu
     *
     * @param file
     */
    public static List<Delta> printDiff(String path, Integer id, Integer ancestor) {
        try {
            List<String> aktualneVerzia = getContent(path, id);
            List<String> staraVerzia = getContent(path, ancestor);

            Patch patch = DiffUtils.diff(staraVerzia, aktualneVerzia);
            Integer additions = 0;
            Integer deletions = 0;
            for (Delta delta : patch.getDeltas()) {
                LOGGER.info("Diff\t" + delta.getType());
                switch (delta.getType()) {
                    case INSERT: {
                        additions += delta.getRevised().getLines().size();
                        LOGGER.info(chunk2String(delta.getRevised()));
                        break;
                    }
                    case DELETE: {
                        deletions += delta.getOriginal().getLines().size();
                        LOGGER.info(chunk2String(delta.getOriginal()));
                        break;
                    }
                    case CHANGE: {
                        int changed = delta.getOriginal().getLines().size() + delta.getRevised().getLines().size();
                        additions += changed;
                        deletions += changed;
                        LOGGER.info(chunk2String(delta.getOriginal()));
                        LOGGER.info(chunk2String(delta.getRevised()));
                        break;
                    }
                }
            }
            LOGGER.info("additions\t" + additions + "\tdeletions\t" + deletions);
            return patch.getDeltas();
        } catch (AstRcsWcfService.NotFoundException|IllegalArgumentException e) {
            LOGGER.warn("Nemozem stiahnut subor, lebo:" + e.getMessage());
        }
        return Collections.emptyList();
    }

    private static String chunk2String(Chunk chunk) {
        List<?> lines = chunk.getLines();
        StringBuilder builder = new StringBuilder(2048);
        builder.append("position:\t").append(chunk.getPosition()).append("\tlines:\t").append(lines.size()).append('\n');
        for (Object o : lines) {
            builder.append(o).append('\n');
        }
        return builder.toString();
    }
}
