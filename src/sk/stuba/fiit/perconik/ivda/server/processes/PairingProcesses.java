package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ProcessesChangedSinceCheckEventDto;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Seky on 7. 8. 2014.
 * <p/>
 * Trieda sa pozera na vytvorene a ukoncene procesy.
 * Pomocou PID procesu hlada zaciatok a ukoncenie procesu.
 * Nasledne danemu procesu priradi aproximovane casi zaciatku a ukoncenia.
 * Mnohe procesy nas nezaujimaju na to sluzi black list procesov
 */
@NotThreadSafe
public class PairingProcesses extends ProcessProcesses implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(PairingProcesses.class.getName());
    private static final long serialVersionUID = 4766155571852827559L;

    private final Map<String, PerUserProcesses> userMapping;

    public PairingProcesses() {
        userMapping = new HashMap<>(16);
    }

    public void printInfo() {
        Set<Map.Entry<String, PerUserProcesses>> set = userMapping.entrySet();
        for (Map.Entry<String, PerUserProcesses> entry : set) {
            String name = entry.getKey();
            PerUserProcesses info = entry.getValue();
            LOGGER.info("Flushing finished processes for " + name);
            info.printFinished();
            LOGGER.info("Flushing unfinished processes for " + name);
            info.printUnfinished();
        }
    }

    public ImmutableList<Process> getFinishedProcesses(String user) {
        PerUserProcesses info = userMapping.get(user);
        if (info == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(info.getFinished());
    }

    protected PerUserProcesses getInfo(String user) {
        PerUserProcesses info = userMapping.get(user);
        if (info == null) {
            info = new PerUserProcesses();
            userMapping.put(user, info);
        }
        return info;
    }

    @Override
    protected void handleStarted(ProcessesChangedSinceCheckEventDto event, ProcessDto started) {
        Process item = new Process();
        item.setStart(event.getTimestamp());
        item.setName(started.getName());
        Process saved = getInfo(event.getUser()).getUnfinished().put(started.getPid(), item);
        if (saved != null) {
            LOGGER.warn("Process s takym PID uz existuje... " + saved);
        }
    }

    @Override
    protected void handleKilled(ProcessesChangedSinceCheckEventDto event, ProcessDto killed) {
        PerUserProcesses info = getInfo(event.getUser());
        Process saved = info.getUnfinished().get(killed.getPid());
        if (saved != null) {
            info.getUnfinished().remove(killed.getPid());
            saved.setEnd(event.getTimestamp());
            found(info, saved);
        }
    }

    public ImmutableMap<String, PerUserProcesses> getUserMapping() {
        return ImmutableMap.copyOf(userMapping);
    }

    public void clearAllUnfinished() {
        Set<Map.Entry<String, PerUserProcesses>> set = userMapping.entrySet();
        for (Map.Entry<String, PerUserProcesses> entry : set) {
            entry.getValue().getUnfinished().clear();
        }
    }

    /**
     * Nasiel sa zaciatok a koniec procesu.
     *
     * @param process
     */
    protected void found(PerUserProcesses info, Process toSave) {
        info.getFinished().add(toSave);
    }

    @Override
    protected void restartProccesses(String user) {
        getInfo(user).getUnfinished().clear();
    }
}
