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
import sk.stuba.fiit.perconik.ivda.astrcs.AstRcsWcfService;
import sk.stuba.fiit.perconik.ivda.cord.client.CordService;
import sk.stuba.fiit.perconik.ivda.cord.dto.Repository;
import sk.stuba.fiit.perconik.ivda.server.EventsUtil;

import java.util.Date;

/**
 * Created by Seky on 21. 10. 2014.
 */
public final class PreprocessEvents extends ProcessEvents2TimelineEvents {

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

        // computeCountOfFileOperations
        String author = event.getUser();
        Date date = event.getTimestamp();
        Integer changedLines = EventsUtil.codeWritten(event.getText());
        String changesetIdInRcs = dokument.getChangesetIdInRcs();
        String servrurl = rcsServer.getUrl();
        LOGGER.info(author + "\t" + date + "\t" + changedLines + "\t" + changesetIdInRcs + "\t" + servrurl + "\t" + path);

        ChangesetDto changeset = null; // 3494
        try {
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
        } catch (AstRcsWcfService.NotFoundException e) {
            e.printStackTrace();
        }

        Repository repo = CordService.getInstance().getNearestRepository(rcsServer.getUrl());      // miraven project neexistje, astrcs ostava

    }

    private void lookAtFileVersions(IdeCodeEventDto event, IdeDocumentDto dokument, sk.stuba.fiit.perconik.ivda.activity.dto.ide.RcsServerDto rcsServer) {

        try {
            LOGGER.info("Skopiroval:\\n" + event.getText());
            RcsServerDto server = AstRcsWcfService.getInstance().getNearestRcsServerDto(rcsServer.getUrl());
            RcsProjectDto project = AstRcsWcfService.getInstance().getRcsProjectDto(server);
            ChangesetDto changeset = AstRcsWcfService.getInstance().getChangesetDto(dokument.getChangesetIdInRcs(), project);
            FileVersionDto fileVersion = AstRcsWcfService.getInstance().getFileVersionDto(changeset, dokument.getServerPath(), project);

            //File file = CordService.getInstance().getFile(repo, commit, path);
            int changedLines = EventsUtil.codeWritten(event.getText());
            if (changedLines > 0) {
                Integer ancestor = fileVersion.getAncestor1Id().getValue();
                add(event, ImmutableMap.of(
                        "uid", event.getEventId(),
                        "path", fileVersion.getUrl().getValue(),
                        "repo", fileVersion.getId(),
                        "commit", ancestor == null ? 0 : ancestor,
                        "changedLines", changedLines
                ));
            } else {
                LOGGER.warn("Prazdne riadky!");
            }
        } catch (AstRcsWcfService.NotFoundException e) {
            LOGGER.error("Chybaju nejake udaje:" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("proccessItem", e);
        }
    }
}
