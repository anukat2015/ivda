package sk.stuba.fiit.perconik.ivda.server.process;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.uaca.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Seky on 10. 8. 2014.
 */
public class BlackListedProcesses implements Serializable {
    private static final Logger logger = Logger.getLogger(BlackListedProcesses.class.getName());
    private static final String fileName = "processBlackList.txt";

    private Set<String> data;

    public BlackListedProcesses() {
        loadBlackList();
    }

    protected void loadBlackList() {
        File file = new File(Configuration.CONFIG_DIR, fileName);
        try {
            data = new HashSet<>(FileUtils.readLines(file));
        } catch (IOException e) {
            logger.error("Error reading from " + fileName, e);
        }
    }

    public boolean contains(ProcessDto process) {
        return data.contains(process.getName());
    }

    public boolean atLeastOneSpecial(List<ProcessDto> list) {
        for (ProcessDto process : list) {
            if (contains(process)) {
                continue;
            }
            // Ide o zaujimavy process
            return true;
        }
        return false;
    }
}
