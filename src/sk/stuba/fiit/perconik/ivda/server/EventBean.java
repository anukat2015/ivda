package sk.stuba.fiit.perconik.ivda.server;


import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Seky on 19. 8. 2014.
 */
@ManagedBean(name = "event")
@ViewScoped()
public class EventBean implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(EventBean.class);

    private Collection<String> files = null;

    public EventBean() {
        LOGGER.info("constr");
    }

    @PostConstruct
    public void init() {
        LOGGER.info("init");
        files = Collections.emptyList();
    }

    public void onLoad() {
        LOGGER.info("onLoad");
        files = Collections.emptyList();
    }

    public Collection<String> getFiles() {
        LOGGER.info("getFiles");
        return files;
    }

    public void setFiles(Collection<String> files) {
        LOGGER.info("setFiles");
        this.files = files;
    }

    public boolean chooseFile(AjaxBehaviorEvent event) {
        LOGGER.info("chooseFile");
        return true;
    }
}
