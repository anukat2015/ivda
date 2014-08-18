package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.io.Files;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
        return FILE_NAME_PATTERN.matcher(file.getUrl().getValue()).replaceAll("_") + '-' + file.getId();
    }

    public static void save(FileVersionDto file) {
        String name = getName(file);
        File cacheFile = new File(CACHE_FOLDER, name);
        if (!cacheFile.exists()) {
            String content = AstRcsWcfService.getInstance().getFileContent(file.getId());
            if (content == null) {
                LOGGER.error("Content not found for:" + name + " because " + file.getContentNotIncludedReason().value());
                return;
            }
            try {
                Files.write(content, cacheFile, Charset.defaultCharset());
                LOGGER.info("Ulozene do cache:" + cacheFile);
            } catch (IOException e) {
                LOGGER.error("saveFileVersion", e);
            }
        }
    }
}
