package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.io.Files;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
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

    public static String getName(FileVersionDto file) {
        return getName(file, file.getId());
    }

    public static String getName(FileVersionDto file, Integer versionID) {
        return FILE_NAME_PATTERN.matcher(file.getUrl().getValue()).replaceAll("_") + '-' + versionID;
    }

    private static List<String> saveContent(FileVersionDto file, Integer id) throws AstRcsWcfService.NotFoundException {
        File cacheFile = new File(CACHE_FOLDER, getName(file, id));
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
        String name = getName(file);
        try {
            return saveContent(file, file.getId());
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.warn("Content probably not found for:" + name + " because " + file.getContentNotIncludedReason().value());
            throw e;
        }
    }

    public static List<String> getContentAncestor(FileVersionDto file) throws AstRcsWcfService.NotFoundException {
        Integer ancestor = file.getAncestor1Id().getValue();
        String name = getName(file, ancestor);
        if (ancestor == null) {
            throw new AstRcsWcfService.NotFoundException("Ancestor is null");
        }
        try {
            return saveContent(file, ancestor);
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.warn("Content probably not found for:" + name + " because " + file.getContentNotIncludedReason().value());
            throw e;
        }
    }
}
