package sk.stuba.fiit.perconik.ivda.server.beans;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import difflib.Delta;
import org.apache.log4j.Logger;
import sk.stuba.fiit.perconik.ivda.activity.client.ActivityService;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCheckinEventDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.util.Diff;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Seky on 19. 8. 2014.
 * <p/>
 * Beana pre events.xhtml stranku.
 */
@ManagedBean(name = "event")
@ViewScoped
public class EventBean implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(EventBean.class);
    private static final long serialVersionUID = 2563906954713653265L;
    private static final ObjectMapper MAPPER;

    @ManagedProperty("#{viewState}")
    private ViewStateBean viewStateBean;

    private List<FileVersionDto> files;
    private FileVersionDto choosedFile;
    private EventDto event;
    private String text;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public EventBean() {
        LOGGER.info("constr");
    }

    private void fileDiff(String id, String ancestor, String path) {
        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(ancestor) || Strings.isNullOrEmpty(path)) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("path / version / old parameter is mandatory")
                            .build()
            );
        }
        viewStateBean.setState(ViewStateBean.ViewState.EVENT);
        try {
            List<String> aktualneVerzia = AstRcsWcfService.getInstance().getContent(path, Integer.valueOf(id));
            List<String> staraVerzia = AstRcsWcfService.getInstance().getContent(path, Integer.valueOf(ancestor));
            List<Delta> deltas = Diff.getDiff(aktualneVerzia, staraVerzia);
            Diff.DiffStats stats = Diff.getStats(deltas);
            text = MAPPER.writeValueAsString(deltas);
        } catch (Exception e) {
            LOGGER.error("json serialize", e);
            text = "error";
        }
    }

    private void eventDownload(String id) {
        if (Strings.isNullOrEmpty(id)) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("sid query parameter is empt")
                            .build()
            );
        }

        event = ActivityService.getInstance().getEvent(id);
        if (event == null) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("Event not found")
                            .build()
            );
        }
    }

    private void eventDetail() {
        viewStateBean.setState(ViewStateBean.ViewState.EVENT);
        try {
            text = MAPPER.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            LOGGER.error("json serialize", e);
            text = "error";
        }
    }

    private void processCheckIn() {
        viewStateBean.setState(ViewStateBean.ViewState.DIFF_FILES);
        IdeCheckinEventDto cevent = (IdeCheckinEventDto) event;

        try {
            sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer = cevent.getRcsServer();
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
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server, cevent.getProjectName());
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

    @PostConstruct
    public void init() {
        LOGGER.info("init");

        String id = FacesUtil.getQueryParam("uid");
        String repo = FacesUtil.getQueryParam("repo");
        String ancestor = FacesUtil.getQueryParam("commit");
        String path = FacesUtil.getQueryParam("path");

        eventDownload(id);
        if (!Strings.isNullOrEmpty(path)) {
            fileDiff(repo, ancestor, path);
            return;
        }
        if (!(event instanceof IdeCheckinEventDto)) {
            eventDetail();
            return;
        }

        processCheckIn();
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
        viewStateBean.setState(ViewStateBean.ViewState.DIFF_FILES);
        return true;
    }

    public FileVersionDto getChoosedFile() {
        return choosedFile;
    }

    public void setChoosedFile(FileVersionDto choosedFile) {
        this.choosedFile = choosedFile;
    }

    public ViewStateBean getViewStateBean() {
        return viewStateBean;
    }

    public void setViewStateBean(ViewStateBean viewStateBean) {
        this.viewStateBean = viewStateBean;
    }

    public EventDto getEvent() {
        return event;
    }

    public void setEvent(EventDto event) {
        this.event = event;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
