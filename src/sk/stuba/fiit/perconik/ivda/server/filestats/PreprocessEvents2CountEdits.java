package sk.stuba.fiit.perconik.ivda.server.filestats;

import com.google.common.base.Strings;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;
import sk.stuba.fiit.perconik.ivda.server.processevents.ProcessEvents;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Date;

/**
 * Created by Seky on 21. 10. 2014.
 * Metoda spracovania udalosti, ktora z ualosti vytiahne operacie nad suborom.
 * Tieto operacie scita a vytvori statistiky.
 */
@NotThreadSafe
public final class PreprocessEvents2CountEdits extends ProcessEvents {
    private final FilesOperationsRepository opRepository;

    public PreprocessEvents2CountEdits() {
        opRepository = new FilesOperationsRepository();
    }

    @Override
    protected void proccessItem(EventDto event) {
        if (event instanceof IdeCodeEventDto) {
            ideEvent((IdeCodeEventDto) event);
            return;
        }
    }

    private void ideEvent(IdeCodeEventDto event) {
        IdeDocumentDto dokument = event.getDocument();
        sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer = dokument.getRcsServer();
        if (rcsServer == null) { // tzv ide o lokalny subor bez riadenia verzii
            return;
        }
        String changesetIdInRcs = dokument.getChangesetIdInRcs();  // 3494
        if (Strings.isNullOrEmpty(changesetIdInRcs) || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            return;
        }
        int changedLines = EventsUtil.codeWritten(event.getText());  // nejde prakticky o ziadnu upravu
        if (changedLines == 0) {
            return;
        }

        fileWasChanged(event, changesetIdInRcs, changedLines);
    }

    private void fileWasChanged(IdeCodeEventDto event, String changesetIdInRcs, int changedLines) {
        String author = event.getUser();
        Date date = event.getTimestamp();
        String path = event.getDocument().getServerPath();
        //LOGGER.info(author + "\t" + date + "\t" + changesetIdInRcs + "\t" + path + "\t" + changedLines);
        opRepository.add(author, path, new FileOperationRecord(date, changedLines));
    }

    public FilesOperationsRepository getOpRepository() {
        return opRepository;
    }
}
