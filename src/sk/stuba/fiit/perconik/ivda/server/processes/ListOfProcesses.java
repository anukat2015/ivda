package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.lang.builder.ToStringBuilder;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.uaca.dto.ProcessDto;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Seky on 10. 8. 2014.
 * <p>
 * Trieda ktora nacitava a stara sa o black-list zoznam procesov.
 */
public final class ListOfProcesses implements Serializable {
    private static final long serialVersionUID = -5261716921783440593L;
    private final Set<String> processes;
    private final String fileName;

    protected ListOfProcesses(String fileName) {
        try {
            this.fileName = fileName;
            File file = new File(Configuration.CONFIG_DIR + File.separator + "processes", fileName);
            processes = new HashSet<>(Files.readLines(file, Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException("ListOfProcesses error reading from file:" + fileName);
        }
    }

    public Set<String> getProcesses() {
        return Collections.unmodifiableSet(processes);
    }

    public boolean contains(ProcessDto process) {
        return contains(process.getName());
    }

    public boolean contains(String process) {
        return processes.contains(process);
    }

    public boolean checkAtLeastOneDontContain(Collection<ProcessDto> list) {
        for (ProcessDto process : list) {
            if (!contains(process)) {
                // Ide o zaujimavy process
                return true;
            }
        }
        return false;
    }

    public Set<String> getSameProcesses(ListOfProcesses list) {
        return Sets.intersection(getProcesses(), list.getProcesses());
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListOfProcesses that = (ListOfProcesses) o;

        if (!fileName.equals(that.fileName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("fileName", fileName).append("processes", processes).toString();
    }

    public static enum Type implements Serializable {
        BLACK_LIST("blackList.txt"),
        COMMUNICATION("communication.txt"),
        TYPICAL("typical.txt"),
        NODEVELOPER("NODEVELOPER.txt");

        ListOfProcesses list;

        private Type(String fileName) {
            list = new ListOfProcesses(fileName);
        }

        public ListOfProcesses getList() {
            return list;
        }
    }
}
