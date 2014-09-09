package sk.stuba.fiit.perconik.ivda.util;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * Created by Seky on 18. 8. 2014.
 * <p/>
 * Pomocne utilitky pre praci s roznymi verziami suborov.
 */
@ThreadSafe
public final class Diff {
    private static final Logger LOGGER = Logger.getLogger(Diff.class.getName());

    /**
     * Dany subor sme nasli, zachyt ID, vypis hodnotu
     *
     * @param file
     */
    public static List<Delta> printDiff(List<String> aktualneVerzia, List<String> staraVerzia) {
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

    public static class DiffStats {
        Integer additions = 0;
        Integer deletions = 0;
    }
}
