package sk.stuba.fiit.perconik.ivda.server;

import org.apache.commons.io.FileUtils;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.uaca.dto.ProcessDto;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Seky on 10. 8. 2014.
 * <p>
 * Trieda ktora nacitava a stara sa o black-list zoznam procesov.
 */
public final class BlackListedProcesses implements Serializable {
    private static final String FILE_NAME = "processBlackList.txt";
    private static final long serialVersionUID = -5261716921783440593L;
    private Set<String> processList;

    public BlackListedProcesses() {
        loadBlackList();
    }

    private void loadBlackList() {
        try {
            File file = new File(Configuration.CONFIG_DIR, FILE_NAME);
            processList = new HashSet<>(FileUtils.readLines(file));
        } catch (IOException e) {
            throw new RuntimeException("BlackListedProcesses error reading from file:" + FILE_NAME);
        }
    }

    public boolean contains(ProcessDto process) {
        return processList.contains(process.getName());
    }

    public boolean checkAtLeastOneAllowed(List<ProcessDto> list) {
        return list.stream().anyMatch(process -> !contains(process));
    }
}
