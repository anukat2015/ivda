package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebNavigateEventDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.server.Catalog;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 7. 8. 2014.
 * <p/>
 * Stiahni file verziu suboru ktory pride v evente..
 */
@NotThreadSafe
public final class ProcessEventsForTimeline extends ProcessEvents2TimelineEvents {
    private final Catalog developerLinks;

    public ProcessEventsForTimeline() {
        developerLinks = Catalog.Processes.BANNED.getList();
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (event instanceof IdeCodeEventDto) {
            ideEvent((IdeCodeEventDto) event);
            return;
        }

        if (event instanceof WebNavigateEventDto) {
            webEvent((WebNavigateEventDto) event);
            return;
        }
    }

    private void webEvent(WebNavigateEventDto event) {
        String link = event.getUrl();
        //if (!developerLinks.contains(link)) {
        //   return;
        //}
        //int changedLines = 20;
        //if (changedLines > 0) {
        add(event, ImmutableMap.of(
                "uid", event.getEventId(),
                "ajax", event
        ));
        //}
    }

    private void ideEvent(IdeCodeEventDto event) {
        IdeDocumentDto dokument = event.getDocument();
        if (event.getStartColumnIndex() != null) {
            LOGGER.warn("ZAUJIMAVE getStartColumnIndex nieje null");
        }
        if (event.getEndColumnIndex() != null) {
            LOGGER.warn("ZAUJIMAVE getEndColumnIndex nieje null");
        }
        if (dokument.getBranch() != null) {
            LOGGER.warn("ZAUJIMAVE getBranch nieje null");
        }

        sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            LOGGER.info("Lokalny subor");
            return;
        }

        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        if (Strings.isNullOrEmpty(changesetIdInRcs) || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            LOGGER.info("changesetIdInRcs empty");
            return;
        }

        try {
            LOGGER.info("Skopiroval:\\n" + event.getText());
            String path = dokument.getServerPath();
            String commit = null;
            String repo = null;
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server, dokument.getServerPath());
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
            //File file = CordService.getInstance().getFile(repo, commit, path);

            // Uloz udaje tak aby ich klient mohol spracovat
            saveEvent(event, fileVersion);
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }

    private void saveEvent(IdeCodeEventDto event, FileVersionDto fileVersion) {
        // Uloz udaje tak aby ich klient mohol spracovat
        Integer changedLines = EventsUtil.codeWritten(event.getText());
        Integer changedInFuture = 0;
        if (changedLines > 0) {
            Integer ancestor = fileVersion.getAncestor1Id().getValue();

            // Vytvore metadata pre Event, tie sa posielaju potom na Ajax detail
            Map<String, Object> map = new HashMap<>();
            map.put("uid", event.getEventId());
            map.put("path", fileVersion.getUrl().getValue());
            map.put("repo", fileVersion.getId().toString());
            map.put("commit", ancestor == null ? 0 : ancestor.toString());
            map.put("changedLines", changedLines.toString());
            map.put("changedInFuture", changedInFuture);
            add(event, map);
        } else {
            LOGGER.warn("Prazdne riadky!");
        }
    }
}
