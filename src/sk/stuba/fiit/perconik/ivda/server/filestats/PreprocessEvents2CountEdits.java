package sk.stuba.fiit.perconik.ivda.server.filestats;

import com.google.common.base.Strings;
import com.gratex.perconik.services.ast.rcs.ChangesetDto;
import com.gratex.perconik.services.ast.rcs.FileVersionDto;
import com.gratex.perconik.services.ast.rcs.RcsProjectDto;
import com.gratex.perconik.services.ast.rcs.RcsServerDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.EventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeCodeEventDto;
import sk.stuba.fiit.perconik.ivda.activity.dto.ide.IdeDocumentDto;
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
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
        String path = dokument.getServerPath();
        if (Strings.isNullOrEmpty(path)) {
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

        fileWasChanged(event, changesetIdInRcs, path, changedLines);
        //lookAtFileVersions(event, dokument, rcsServer);
        //Repository repo = CordService.getInstance().getNearestRepository(rcsServer.getUrl());      // miraven project neexistje, astrcs tak musi ostat
    }

    private void fileWasChanged(IdeCodeEventDto event, String changesetIdInRcs, String path, int changedLines) {
        String author = event.getUser();
        Date date = event.getTimestamp();
        //LOGGER.info(author + "\t" + date + "\t" + changesetIdInRcs + "\t" + path + "\t" + changedLines);
        opRepository.add(author, path, new FileOperationRecord(date, changedLines));
    }

    private void lookAtFileVersions(IdeCodeEventDto event, IdeDocumentDto dokument, sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer) {

        try {
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server, dokument.getServerPath());
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);
            ChangesetDto successorChangeset = AstRcsWcfService.getInstance().getChangesetSuccessor(changeset, fileVersion);
            FileVersionDto successorfileVersion = AstRcsWcfService.getInstance().getFileVersionSuccessor(successorChangeset, fileVersion);

            if (!fileVersion.getUrl().getValue().equals(successorfileVersion.getUrl().getValue())) {
                LOGGER.warn("Nesedia cesty.");
            }

        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        }
    }

    public FilesOperationsRepository getOpRepository() {
        return opRepository;
    }
}
