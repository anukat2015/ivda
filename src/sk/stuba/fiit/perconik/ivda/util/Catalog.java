package sk.stuba.fiit.perconik.ivda.util;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Seky on 10. 8. 2014.
 * <p/>
 * Trieda ktora nacitava a stara sa o blacklist / witelist zoznam.
 */
@ThreadSafe
public final class Catalog implements Serializable {
    private static final long serialVersionUID = -5261716921783440593L;
    private final Set<String> catalog;
    private final String fileName;

    protected Catalog(String fileName) {
        try {
            this.fileName = fileName;
            File file = new File(Configuration.CONFIG_DIR + File.separator + "catalog", fileName);
            catalog = new HashSet<>(Files.readLines(file, Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException("Catalog error reading from file:" + fileName);
        }
    }

    public static void staticInit() {

    }

    public Set<String> getData() {
        return Collections.unmodifiableSet(catalog);
    }

    public boolean exist(String process) {
        return catalog.contains(process);
    }

    public boolean checkAtLeastOneDontExist(Iterator<String> list) {
        while (list.hasNext()) {
            if (!exist(list.next())) {
                // Ide o zaujimavy process
                return true;
            }
        }
        return false;
    }

    public Set<String> getSameValues(Catalog list) {
        return Sets.intersection(getData(), list.getData());
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Catalog that = (Catalog) o;

        if (!fileName.equals(that.fileName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("fileName", fileName).append("catalog", catalog).toString();
    }

    public enum Processes implements Serializable {
        BANNED("banned.txt"),
        COMMUNICATION("communication.txt"),
        TYPICAL("typical.txt"),
        NODEVELOPER("nodeveloper.txt"),
        MUSIC("music.txt");

        Catalog list;

        Processes(String fileName) {
            list = new Catalog("processes" + File.separator + fileName);
        }

        public Catalog getList() {
            return list;
        }

        public static Processes determine(String code) {
            for (Processes type : Processes.values()) {
                if (type.getList().exist(code)) {
                    return type;
                }
            }
            return null;
        }

        public static String classify(String code) {
            Processes type = determine(code);
            if (type != null) {
                return type.toString();
            }
            return null;
        }
    }

    public enum Web implements Serializable {
        WORK("work.txt"),
        PERSONAL("personal.txt"),
        UNKNOWN("unknown.txt");

        Catalog list;

        Web(String fileName) {
            list = new Catalog("web" + File.separator + fileName);
        }

        public Catalog getList() {
            return list;
        }

        /**
         * Vrat typ Webu podla toho kde sa kod nachadza.
         * Prepodkladame, ze jeden kod je len v jednom subore.
         *
         * @param code
         * @return
         */
        public static Web determine(String code) {
            for (Web type : Web.values()) {
                if (type.getList().exist(code)) {
                    return type;
                }
            }
            return null;
        }

        public static String classify(String code) {
            Web type = determine(code);
            if (type != null) {
                return type.toString();
            }
            return null;
        }
    }
}
