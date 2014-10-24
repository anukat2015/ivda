package sk.stuba.fiit.perconik.ivda.server;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

    public boolean exist(ProcessDto process) {
        return exist(process.getName());
    }

    public boolean exist(String process) {
        return catalog.contains(process);
    }

    public boolean checkAtLeastOneDontExist(Collection<ProcessDto> list) {
        for (ProcessDto process : list) {
            if (!exist(process)) {
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
        NODEVELOPER("nodeveloper.txt");

        Catalog list;

        Processes(String fileName) {
            list = new Catalog("processes" + File.separator + fileName);
        }

        public Catalog getList() {
            return list;
        }
    }

    public enum Web implements Serializable {
        DEVELOPER("developer.txt");

        Catalog list;

        Web(String fileName) {
            list = new Catalog("web" + File.separator + fileName);
        }

        public Catalog getList() {
            return list;
        }
    }
}
