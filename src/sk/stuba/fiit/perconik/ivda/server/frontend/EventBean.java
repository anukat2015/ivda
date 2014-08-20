package sk.stuba.fiit.perconik.ivda.server.frontend;


import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.uaca.dto.EventDto;
import sk.stuba.fiit.perconik.uaca.dto.ide.IdeCheckinEventDto;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Seky on 19. 8. 2014.
 * <p>
 * Beana pre events.xhtml stranku.
 */
@ManagedBean(name = "event")
@ViewScoped
public class EventBean implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(EventBean.class);

    private List<FileVersionDto> files;
    private FileVersionDto choosedFile;

    public EventBean() {
        LOGGER.info("constr");
    }

    @PostConstruct
    public void init() {
        LOGGER.info("init");

        String sid = FacesUtil.getQueryParam("id");
        if (sid == null) {
            FacesUtil.addMessage("sid query parameter is empty", FacesMessage.SEVERITY_ERROR);
            return;
        }

        EventDto event = EventsUtil.download(sid);
        if (event == null) {
            FacesUtil.addMessage("Event not found", FacesMessage.SEVERITY_INFO);
            return;
        }

        if (!(event instanceof IdeCheckinEventDto)) {
            return;
        }
        IdeCheckinEventDto cevent = (IdeCheckinEventDto) event;

        try {
            sk.stuba.fiit.perconik.uaca.dto.ide.RcsServerDto rcsServer = cevent.getRcsServer();
            if (rcsServer == null) {
                // tzv ide o lokalny subor bez riadenia verzii
                throw new AstRcsWcfService.NotFoundException("rcsServer is empty");
            }

            String changesetIdInRcs = cevent.getChangesetIdInRcs();
            if (changesetIdInRcs.isEmpty() || changesetIdInRcs.compareTo("0") == 0) {
                // changeset - teda commit id nenajdeny
                throw new AstRcsWcfService.NotFoundException("changesetIdInRcs empty");
            }

            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(changesetIdInRcs, project);
            files = AstRcsWcfService.getInstance().getChangedFiles(changeset);
            Comparator<FileVersionDto> comparator = new Comparator<FileVersionDto>() {
                @Override
                public int compare(FileVersionDto o1, FileVersionDto o2) {
                    return o1.getUrl().getValue().compareTo(o2.getUrl().getValue());
                }
            };
            Collections.sort(files, comparator);
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.info("Chybaju nejake udaje:" + e.getMessage());
        }
    }

    public Collection<FileVersionDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileVersionDto> files) {
        LOGGER.info("setFiles");
        this.files = files;
    }

    public boolean chooseFile(AjaxBehaviorEvent event) {
        LOGGER.info("chooseFile");
        return true;
    }

    public FileVersionDto getChoosedFile() {
        return choosedFile;
    }

    public void setChoosedFile(FileVersionDto choosedFile) {
        this.choosedFile = choosedFile;
    }
}
