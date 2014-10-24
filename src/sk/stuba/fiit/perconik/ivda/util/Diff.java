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
    public static List<Delta> getDiff(List<String> aktualneVerzia, List<String> staraVerzia) {
        Patch patch = DiffUtils.diff(staraVerzia, aktualneVerzia);
        return patch.getDeltas();
    }

    @SuppressWarnings("UnnecessaryCodeBlock")
    public static DiffStats getStats(List<Delta> deltas) {
        DiffStats stat = new DiffStats();
        for (Delta delta : deltas) {
            switch (delta.getType()) {
                case INSERT: {
                    stat.additions += delta.getRevised().getLines().size();
                    //LOGGER.info(chunk2String(delta.getRevised()));
                    break;
                }
                case DELETE: {
                    stat.deletions += delta.getOriginal().getLines().size();
                    //LOGGER.info(chunk2String(delta.getOriginal()));
                    break;
                }
                case CHANGE: {
                    int changed = delta.getOriginal().getLines().size() + delta.getRevised().getLines().size();
                    stat.additions += changed;
                    stat.deletions += changed;
                    //LOGGER.info(chunk2String(delta.getOriginal()));
                    //LOGGER.info(chunk2String(delta.getRevised()));
                    break;
                }
            }
        }
        return stat;
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
        public Integer additions = 0;
        public Integer deletions = 0;
    }
}
