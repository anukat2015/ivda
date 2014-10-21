package sk.stuba.fiit.perconik.ivda.server.processevents;

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

import java.util.Date;

/**
 * Created by Seky on 21. 10. 2014.
 */
public final class PreprocessEvents extends ProcessEvents {

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
            //LOGGER.info("Lokalny subor");
            return;
        }
        String path = dokument.getServerPath();
        if (Strings.isNullOrEmpty(path)) {
            //LOGGER.info("Path is empty");
            return;
        }

        String changesetIdInRcs = dokument.getChangesetIdInRcs();  // 3494
        if (Strings.isNullOrEmpty(changesetIdInRcs) || changesetIdInRcs.compareTo("0") == 0) { // changeset - teda commit id nenajdeny
            //LOGGER.info("changesetIdInRcs empty");
            return;
        }

        // computeCountOfFileOperations
        String author = event.getUser();
        Date date = event.getTimestamp();
        Integer changedLines = EventsUtil.codeWritten(event.getText());
        String servrurl = rcsServer.getUrl();
        //LOGGER.info(author + "\t" + date + "\t" + changedLines + "\t" + changesetIdInRcs + "\t" + servrurl + "\t" + path + "\t" + event);

        lookAtFileVersions(event, dokument, rcsServer);
        //Repository repo = CordService.getInstance().getNearestRepository(rcsServer.getUrl());      // miraven project neexistje, astrcs ostava

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
}
