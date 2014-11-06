package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.BashCommandEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebNavigateEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebTabEventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.filestats.FilesOperationsRepository;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 7. 8. 2014.
 * <p/>
 * Metoda spracovania udalosti, ktora posiela vyslene informacie o timeline.
 */
@NotThreadSafe
public final class ProcessEventsForTimeline extends ProcessEvents2TimelineEvents {
    private static final FilesOperationsRepository OP_REPOSITORY;
    //private final Catalog developerLinks;

    static {
        Configuration.getInstance();
        File processesFile = new File(Configuration.CONFIG_DIR, "fileOperations.gzip");
        try {
            OP_REPOSITORY = (FilesOperationsRepository) GZIP.deserialize(processesFile);
        } catch (Exception e) {
            throw new RuntimeException(processesFile + " file is missing.");
        }
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (event instanceof WebTabEventDto) { // ako dlho stravil na konkretnej stranke
            return;
        }
        if (event instanceof BashCommandEventDto) {
            return;
        }

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
                "link", link
        ));
        //}
    }

    private void ideEvent(IdeCodeEventDto event) {
        IdeDocumentDto dokument = event.getDocument();
        RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            return;
        }
        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        if (Strings.isNullOrEmpty(changesetIdInRcs) || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            return;
        }

        try {
            FileVersionDto fileVersion = null;
            //LOGGER.info("Skopiroval:\\n" + event.getText());
            //RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            //RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server, dokument.getServerPath());
            //ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            //FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
            //File file = CordService.getInstance().getFile(repo, commit, path);

            // Uloz udaje tak aby ich klient mohol spracovat
            saveEvent(event, fileVersion);
            //} catch (AstRcsWcfService.NotFoundException e) {
            //    LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }

    private void saveEvent(IdeCodeEventDto event, FileVersionDto fileVersion) {
        // Uloz udaje tak aby ich klient mohol spracovat
        Integer changedLines = EventsUtil.codeWritten(event.getText());
        if (changedLines > 0) {
            //Integer ancestor = fileVersion.getAncestor1Id().getValue();
            FilesOperationsRepository.CountOperations stats = OP_REPOSITORY.countOperationsAfter(event.getDocument().getServerPath(), event.getTimestamp());

            // Vytvore metadata pre Event, tie sa posielaju potom na Ajax detail
            Map<String, Object> metadata = new HashMap<>(8);
            metadata.put("uid", event.getEventId());
            //metadata.put("path", fileVersion.getUrl().getValue());
            metadata.put("path", event.getDocument().getServerPath());
            metadata.put("text", event.getText());
            //metadata.put("repo", fileVersion.getId());
            // metadata.put("commit", ancestor == null ? 0 : ancestor);   //! Nepridavat .toString(, lebo javascript to nacitava ako cislo
            metadata.put("changedLines", changedLines);
            metadata.put("changedInFuture", stats.after);
            metadata.put("changedInHistory", stats.before);
            add(event, metadata);
        }
    }
}
