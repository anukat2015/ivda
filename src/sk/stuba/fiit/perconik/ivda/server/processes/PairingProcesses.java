package sk.stuba.fiit.perconik.ivda.server.processes;

import com.google.common.collect.ImmutableList;
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
 * Mnohe procesy nas nezaujimaju na to sluzi black lis procesov
 */
@NotThreadSafe
public class PairingProcesses extends ProcessProcesses implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(PairingProcesses.class.getName());
    private static final long serialVersionUID = 4766155571852827559L;

    private final Map<String, PerUserInfo> userMapping;

    public PairingProcesses() {
        userMapping = new HashMap<>(16);
    }

    public void printInfo() {
        Set<Map.Entry<String, PerUserInfo>> set = userMapping.entrySet();
        for (Map.Entry<String, PerUserInfo> entry : set) {
            LOGGER.info("Flushing finished processes for " + entry.getKey());
            entry.getValue().printFinished();
            LOGGER.info("Flushing unfinished processes for " + entry.getKey());
            entry.getValue().printUnfinished();
        }
    }

    public ImmutableList<Process> getFinishedProcesses(String user) {
        PerUserInfo info = userMapping.get(user);
        if (info == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(info.getFinished());
    }

    protected PerUserInfo getInfo(String user) {
        PerUserInfo info = userMapping.get(user);
        if (info == null) {
            info = new PerUserInfo();
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
        PerUserInfo info = getInfo(event.getUser());
        Process saved = info.getUnfinished().get(killed.getPid());
        if (saved != null) {
            info.getUnfinished().remove(killed.getPid());
            saved.setEnd(event.getTimestamp());
            found(info, saved);
        }
    }

    /**
     * Nasiel sa zaciatok a koniec procesu.
     *
     * @param process
     */
    protected void found(PerUserInfo info, Process toSave) {
        info.getFinished().add(toSave);
    }

    @Override
    protected void restartProccesses(String user) {
        getInfo(user).clear();
    }
}
