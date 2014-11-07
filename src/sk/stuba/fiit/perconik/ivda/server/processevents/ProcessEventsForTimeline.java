package sk.stuba.fiit.perconik.ivda.server.processevents;

import com.google.common.base.Strings;
import sk.stuba.fiit.perconik.ivda.activity.dto.BashCommandEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.web.WebNavigateEventDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.filestats.FilesOperationsRepository;
import sk.stuba.fiit.perconik.ivda.util.Configuration;
import sk.stuba.fiit.perconik.ivda.util.lang.GZIP;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seky on 7. 8. 2014.
 * <p/>
 * Metoda spracovania udalosti, ktora posiela vyslene informacie o timeline.
 */
@NotThreadSafe
public final class ProcessEventsForTimeline extends ProcessEventsOut {
    private static final FilesOperationsRepository OP_REPOSITORY;

    public ProcessEventsForTimeline(OutputStream out) {
        super(out);
    }

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
        if (event instanceof BashCommandEventDto) {
            bashEvent((BashCommandEventDto) event);
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

    private void bashEvent(BashCommandEventDto event) {
        add(event, event.getCommandLine(), null, null);
    }

    private void webEvent(WebNavigateEventDto event) {
        String link = event.getUrl();
        add(event, link, null, null);
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
        saveEvent(event);
    }

    private void saveEvent(IdeCodeEventDto event) {
        // Uloz udaje tak aby ich klient mohol spracovat
        Integer changedLines = EventsUtil.codeWritten(event.getText());
        if (changedLines > 0) {
            //Integer ancestor = fileVersion.getAncestor1Id().getValue();
            FilesOperationsRepository.CountOperations stats = OP_REPOSITORY.countOperationsAfter(event.getDocument().getServerPath(), event.getTimestamp());

            // Vytvore metadata pre Event, tie sa posielaju potom na Ajax detail
            Map<String, Object> metadata = new HashMap<>(8);
            metadata.put("text", event.getText());
            //metadata.put("repo", fileVersion.getId());
            // metadata.put("commit", ancestor == null ? 0 : ancestor);   //! Nepridavat .toString(, lebo javascript to nacitava ako cislo
            metadata.put("changedInFuture", stats.after);
            metadata.put("changedInHistory", stats.before);
            add(event, event.getDocument().getServerPath(), changedLines, metadata);
        }
    }
}
