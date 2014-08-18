package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.io.Files;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Seky on 18. 8. 2014.
 * <p>
 * Pomocne utilitky pre praci s roznymi verziami suborov.
 */
public final class FileVersionsUtil {
    private static final Logger LOGGER = Logger.getLogger(FileVersionsUtil.class.getName());
    private final static File CACHE_FOLDER = Configuration.getInstance().getCacheFolder();

    public static void save(FileVersionDto file) {
        Integer id = file.getId();
        String name = Files.getNameWithoutExtension(file.getUrl().getValue()) + id;
        File cacheFile = new File(CACHE_FOLDER, name);
        LOGGER.info("Ulozene do cache:" + cacheFile);
        if (!cacheFile.exists()) {
            String content = AstRcsWcfService.getInstance().getFileContent(id);
            if (content == null) {
                LOGGER.error("Content not found for:" + name + " because " + file.getContentNotIncludedReason().value());
                return;
            }
            try {
                Files.write(content, cacheFile, Charset.defaultCharset());
            } catch (IOException e) {
                LOGGER.error("saveFileVersion", e);
            }
        }
    }
}
